package com.paperized.shopapi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.paperized.shopapi.dquery.DQueriable;
import com.paperized.shopapi.dto.WebhookScrapeInput;
import com.paperized.shopapi.exceptions.TrackingAlreadyScheduledException;
import com.paperized.shopapi.exceptions.TrackingExpiredException;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import com.paperized.shopapi.model.ProductTracker;
import com.paperized.shopapi.model.ProductTrackerDetails;
import com.paperized.shopapi.repository.ProductTrackerRepository;
import com.paperized.shopapi.repository.ProductTrackerDetailsRepository;
import com.paperized.shopapi.scraper.ScrapeExecutor;
import com.paperized.shopapi.service.ProductTrackerScheduler;
import com.paperized.shopapi.service.ScrapingActionService;
import com.paperized.shopapi.utils.AppUtils;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
                    Instant.now().plusSeconds(60),
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

        if(force && alreadyScheduled) {
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

                // registeredTracking.getFilters().isOnlyIfDifferent()
                // registeredTracking.getLastScrapedDataAs(tracking.getAction()) -> LAST_DATA
                // check flag isOnlyIfDifferent and if LAST_DATA != scrapedResult
                // if single -> .equals | if multiple -> check if there is any change in entries between last data and current, for each item check the UniqueIdentifier and check the corrisponding item in the other list
                Object prevData = registeredTracking.getLastScrapedDataAs(tracking.getAction());
                boolean prevDataChanged = false;

                if (hasFilters && registeredTracking.getFilters().isOnlyIfDifferent()) {
                    if (prevData != null) {
                        if (returnsList) {
                            List<? extends DQueriable> convertedPrevData = (List<? extends DQueriable>) prevData;
                            List<? extends DQueriable> scrapedList = (List<? extends DQueriable>) scrapedResult;

                            if (scrapedList == null ^ convertedPrevData == null) {
                                log.info("One of the two lists are empty/null!");
                                prevDataChanged = true;
                            } else if (convertedPrevData.size() != scrapedList.size()) {
                                log.info("Lists have different sizes!");
                                prevDataChanged = true;
                            } else {
                                boolean sameList = scrapedList.stream().allMatch(curr -> {
                                    DQueriable other = convertedPrevData.stream()
                                            .filter(x -> org.apache.commons.lang3.StringUtils.equals(x.getUniqueIdentifier(), curr.getUniqueIdentifier()))
                                            .findFirst().orElse(null);

                                    return other != null && other.equals(curr);
                                });

                                prevDataChanged = !sameList;
                            }

                        } else {
                            prevDataChanged = scrapedResult.equals(prevData);
                        }

                        if (!prevDataChanged) {
                            log.info("Previous data was equal to the current one scraped, webhook will not be called due to filters!");
                            return;
                        }

                    } else {
                        prevDataChanged = true;
                    }
                }

                if (prevDataChanged) {
                    registeredTracking.setLastScrapedDataJson(AppUtils.toJson(scrapedResult));
                    productTrackerDetailsRepository.save(registeredTracking);
                }

                String resultTest = AppUtils.toJson(scrapedResult);
                log.info(resultTest);

                makeWebhookRequest(trackerId, registeredTracking.getWebhookUrl(), scrapedResult);
            } catch (HttpStatusException | UnsuccessfulScrapeException e) {
                log.error(e.getMessage());
            } catch (JsonProcessingException e) {
                log.error("Logging test result failed: {}", e.getMessage());
            }
        };
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
}
