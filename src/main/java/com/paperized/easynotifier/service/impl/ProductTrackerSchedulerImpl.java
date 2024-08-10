package com.paperized.easynotifier.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.paperized.easynotifier.dquery.DQueriable;
import com.paperized.easynotifier.dto.WebhookScrapeInput;
import com.paperized.easynotifier.exceptions.NoDataException;
import com.paperized.easynotifier.exceptions.TrackingAlreadyScheduledException;
import com.paperized.easynotifier.exceptions.TrackingExpiredException;
import com.paperized.easynotifier.exceptions.UnsuccessfulScrapeException;
import com.paperized.easynotifier.model.ProductTracker;
import com.paperized.easynotifier.model.ProductTrackerDetails;
import com.paperized.easynotifier.model.webhookfilter.DOnChangeMode;
import com.paperized.easynotifier.model.webhookfilter.DOnChangeSaveMode;
import com.paperized.easynotifier.model.webhookfilter.DOnChanges;
import com.paperized.easynotifier.repository.ProductTrackerRepository;
import com.paperized.easynotifier.repository.ProductTrackerDetailsRepository;
import com.paperized.easynotifier.service.ProductTrackerScheduler;
import com.paperized.easynotifier.service.ScrapingActionService;
import com.paperized.easynotifier.utils.AppUtils;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static java.lang.String.format;

@Slf4j
@Service
public class ProductTrackerSchedulerImpl implements ProductTrackerScheduler {
    private final Map<String, ScheduledFuture<?>> currentScheduledProductTracking = new ConcurrentHashMap<>();
    private final ScrapingActionService scrapingActionService;
    private final ProductTrackerRepository productTrackerRepository;
    private final ProductTrackerDetailsRepository productTrackerDetailsRepository;
    private final TaskScheduler taskScheduler;
    private final Long defaultRegisteredExpireInMinutes;

    public ProductTrackerSchedulerImpl(@Value("${tracking.defaultRegistrationExpireTime}") Long defaultRegisteredExpireInMinutes, ScrapingActionService scrapingActionService,
                                       ProductTrackerRepository productTrackerRepository, ProductTrackerDetailsRepository productTrackerDetailsRepository,
                                       TaskScheduler taskScheduler) {
        this.scrapingActionService = scrapingActionService;
        this.productTrackerRepository = productTrackerRepository;
        this.defaultRegisteredExpireInMinutes = defaultRegisteredExpireInMinutes;
        this.productTrackerDetailsRepository = productTrackerDetailsRepository;
        this.taskScheduler = taskScheduler;
    }

    @PostConstruct
    public void rescheduleProductTrackings() {
        List<ProductTrackerDetails> trackingList = productTrackerDetailsRepository.findAll();
        for (ProductTrackerDetails tracking : trackingList) {
            Runnable runnable = getProductTrackingRunnable(tracking.getId());

            // Restart each schedule after 1 minute after the startup
            ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleWithFixedDelay(runnable,
                    Instant.now().plusSeconds(5),
                    Duration.ofMillis(tracking.getIntervalDuration()));

            currentScheduledProductTracking.put(tracking.getId(), scheduledFuture);
            log.info("Rescheduled tracking webhook successfully with id : {}!", tracking.getId());
        }
    }

    @Override
    public void scheduleTracker(final String trackerId, boolean force) throws TrackingAlreadyScheduledException, TrackingExpiredException {
        boolean alreadyScheduled = currentScheduledProductTracking.containsKey(trackerId);
        if (!force && alreadyScheduled) {
            throw new TrackingAlreadyScheduledException(format("Request of tracking schedule but Tracking id: %s was already scheduled!", trackerId));
        }

        ProductTracker productTracker = productTrackerRepository.findById(trackerId)
                .orElseThrow(() -> new EntityNotFoundException(format("Schedule requested but the Tracking id: %s no longed exists!", trackerId)));

        if (force && alreadyScheduled) {
            ScheduledFuture<?> scheduledFuture = currentScheduledProductTracking.remove(trackerId);
            scheduledFuture.cancel(false);
            log.info("Unregistered tracking webhook because of force schedule with id : {}!", trackerId);
        }

        Runnable runnable = getProductTrackingRunnable(trackerId);
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleWithFixedDelay(runnable,
                Instant.now().plusMillis(productTracker.getProductTrackerDetails().getIntervalDuration()),
                Duration.ofMillis(productTracker.getProductTrackerDetails().getIntervalDuration()));

        currentScheduledProductTracking.put(trackerId, scheduledFuture);
        log.info("Registered tracking webhook successfully with id : {}!", trackerId);
    }

    @Override
    public void unscheduleTracker(String trackerId) {
        if (!currentScheduledProductTracking.containsKey(trackerId)) {
            return;
        }

        /*
            ProductTracker productTracker = productTrackerRepository.findById(trackerId).orElseThrow(
                () -> new EntityNotFoundException(format("Tracking with id: %s no longer exists!", trackerId)));

                TODO: maybe add a disable button
         */

        ScheduledFuture<?> scheduledFuture = currentScheduledProductTracking.remove(trackerId);
        scheduledFuture.cancel(false);
        log.info("Unregistered tracking webhook successfully with id : {}!", trackerId);
    }

    private Runnable getProductTrackingRunnable(String trackerId) {
        return () -> {
            Optional<ProductTracker> trackerOptional = productTrackerRepository.findById(trackerId);
            if (trackerOptional.isEmpty()) {
                log.error("Tracking with id: {} no longer exists!", trackerId);
                return;
            }

            ProductTracker tracking = trackerOptional.get();
            ProductTrackerDetails registeredTracking = tracking.getProductTrackerDetails();
            if (registeredTracking == null) {
                log.error("Tracking with id: {} has no registered webhook!", trackerId);
                return;
            }

            Object scrapedResult;
            try {
                scrapedResult = scrapingActionService.replicateScrapeAction(tracking.getUrl(), tracking.getWebsiteName(), tracking.getAction());
                log.info("Successfully scraped product with tracking id: {}", trackerId);

                boolean returnsList = tracking.getAction().isReturnsList();
                boolean hasFilters = registeredTracking.getFilters() != null;
                if (returnsList && hasFilters) {
                    List<? extends DQueriable> scrapedList = (List<? extends DQueriable>) scrapedResult;
                    registeredTracking.getFilters().filterQueriables(scrapedList);
                    log.info("Filtered scheduled scraped list with tracking id: {}", trackerId);
                }

                if ((!returnsList && scrapedResult == null) || (returnsList && CollectionUtils.isEmpty((List<? extends DQueriable>) scrapedResult))) {
                    throw new NoDataException("Scraper did not find any content!");
                }

                Object prevData = registeredTracking.getLastScrapedDataAs(tracking.getAction());
                Object dataChanged = scrapedResult;
                Object allData = scrapedResult;

                DOnChangeMode sendMode = DOnChangeMode.SEND_ALL_ONLY_IF_DIFF;
                DOnChangeSaveMode saveMode = DOnChangeSaveMode.SAVE_LAST;

                if (hasFilters && registeredTracking.getFilters().getPreviousDataChecks() != null) {
                    DOnChanges checks = registeredTracking.getFilters().getPreviousDataChecks();
                    saveMode = checks.getSaveMode();
                    sendMode = checks.getMode();

                    ChangedData differences = checkQueriablesDifferences(returnsList, prevData, scrapedResult, checks.getPropertiesToCheck());

                    if (!differences.hasChanges) {
                        log.info("Previous data was equal to the current one scraped, webhook will not be called due to filters!");
                        return;
                    }

                    dataChanged = differences.changedData;
                    allData = differences.allData;
                }

                Object dataToSave = DOnChangeSaveMode.SAVE_ALL.equals(saveMode) ? allData : dataChanged;
                if (dataToSave != null) {
                    registeredTracking.setLastScrapedDataJson(AppUtils.toJson(dataToSave));
                    productTrackerDetailsRepository.save(registeredTracking);

                    String resultTest = AppUtils.toJson(scrapedResult);
                    log.info(resultTest);
                }

                Object dataToSend = DOnChangeMode.SEND_ALL_ONLY_IF_DIFF.equals(sendMode) ? allData : dataChanged;
                makeWebhookRequest(trackerId, registeredTracking.getWebhookUrl(), dataToSend);
            } catch (HttpStatusException | UnsuccessfulScrapeException e) {
                log.error(e.getMessage());
            } catch (JsonProcessingException e) {
                log.error("Logging test result failed: {}", e.getMessage());
            }
        };
    }

    private ChangedData checkQueriablesDifferences(boolean returnsList, Object prevData, Object scrapedResult, List<String> propertiesToCheck) {
        boolean prevDataChanged;
        Object changedData = null;
        Object allData = null;

        if (prevData != null) {
            if (returnsList) {
                List<? extends DQueriable> convertedPrevData = (List<? extends DQueriable>) prevData;
                List<? extends DQueriable> scrapedList = (List<? extends DQueriable>) scrapedResult;

                if (CollectionUtils.isEmpty(scrapedList)) {
                    throw new NoDataException("No data scraped, not sending any webhook");
                }

                if (CollectionUtils.isEmpty(convertedPrevData)) {
                    log.info("One of the two lists are empty/null!");
                    prevDataChanged = true;
                    changedData = scrapedResult;
                    allData = scrapedList;
                } else {
                    ChangedData merge = mergeTwoQueriables(convertedPrevData, scrapedList, propertiesToCheck);
                    prevDataChanged = merge.hasChanges;
                    changedData = merge.changedData;
                    allData = merge.allData;
                }
            } else {
                prevDataChanged = !scrapedResult.equals(prevData);
                changedData = scrapedResult;
                allData = scrapedResult;
            }
        } else {
            prevDataChanged = true;
            changedData = scrapedResult;
            allData = scrapedResult;
        }

        return new ChangedData(prevDataChanged, changedData, allData);
    }

    private ChangedData mergeTwoQueriables(List<? extends DQueriable> lPrio, List<? extends DQueriable> hPrio, List<String> propertiesToCheck) {
        boolean hasChanged = false;
        List<DQueriable> changes = new ArrayList<>();
        List<DQueriable> allData = new ArrayList<>(hPrio);

        for (DQueriable l : lPrio) {
            Optional<? extends DQueriable> found = hPrio.stream().filter(x -> x.getUniqueIdentifier().equals(l.getUniqueIdentifier())).findFirst();
            if (found.isEmpty()) {
                changes.add(l);
                allData.add(l);
                continue;
            }

            DQueriable obj = found.get();
            if (CollectionUtils.isEmpty(propertiesToCheck)) {
                if (!obj.equals(l)) {
                    changes.add(obj);
                    hasChanged = true;
                }
            } else {
                boolean currHasChanged;

                for(String prop : propertiesToCheck) {
                    currHasChanged = !Objects.equals(obj.getVariableValue(prop), l.getVariableValue(prop));
                    if(currHasChanged) {
                        changes.add(obj);
                        hasChanged = true;
                        break;
                    }
                }
            }

        }

        return new ChangedData(hasChanged, changes, allData);
    }

    private void makeWebhookRequest(String trackerId, String webhook, Object body) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.postForLocation(URI.create(webhook), WebhookScrapeInput.builder().result(body).trackingId(trackerId).build());
            log.info("Successfully scraped product with tracking id: {} and sent via webhook", trackerId);
        } catch (RestClientException ex) {
            // implement max retries before deleting the schedule
            log.info("Failed to send scraped result via webhook (trackingId: {}, webhookUrl: {}, message: {})",
                    trackerId, webhook, ex.getMessage());
        }
    }


    private record ChangedData(boolean hasChanges, Object changedData, Object allData) {
    }
}
