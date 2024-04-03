package com.paperized.shopapi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperized.shopapi.dquery.DQueriable;
import com.paperized.shopapi.dto.DQueryRequestWebhook;
import com.paperized.shopapi.dto.WebhookScrapeInput;
import com.paperized.shopapi.exceptions.TrackingAlreadyScheduledException;
import com.paperized.shopapi.exceptions.TrackingExpiredException;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import com.paperized.shopapi.model.ProductTracking;
import com.paperized.shopapi.model.RegisteredProductTracking;
import com.paperized.shopapi.model.TrackingAction;
import com.paperized.shopapi.model.WebsiteName;
import com.paperized.shopapi.repository.ProductTrackingRepository;
import com.paperized.shopapi.repository.RegisteredProductTrackingRepository;
import com.paperized.shopapi.scraper.ScrapeExecutor;
import com.paperized.shopapi.service.ScrapingActionService;
import com.paperized.shopapi.utils.AppUtils;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.jsoup.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import static java.lang.String.format;

@Service
public class ScrapingActionServiceImpl implements ScrapingActionService {
    private final Logger logger = LoggerFactory.getLogger(ScrapingActionServiceImpl.class);
    private final Map<String, ScheduledFuture<?>> currentScheduledProductTracking = new HashMap<>();
    private final Map<WebsiteName, ScrapeExecutor> scrapeExecutorMap = new HashMap<>();
    private final ProductTrackingRepository productTrackingRepository;
    private final RegisteredProductTrackingRepository registeredProductTrackingRepository;
    private final TaskScheduler taskScheduler;
    private final Long defaultRegisteredExpireInMinutes;

    public ScrapingActionServiceImpl(@Value("${tracking.defaultRegistrationExpireTime}") Long defaultRegisteredExpireInMinutes, List<ScrapeExecutor> scrapeExecutors,
                                     ProductTrackingRepository productTrackingRepository, RegisteredProductTrackingRepository registeredProductTrackingRepository, TaskScheduler taskScheduler) {
        this.productTrackingRepository = productTrackingRepository;
        this.defaultRegisteredExpireInMinutes = defaultRegisteredExpireInMinutes;
        this.registeredProductTrackingRepository = registeredProductTrackingRepository;
        this.taskScheduler = taskScheduler;
        scrapeExecutors.forEach(x -> {
            ScrapeExecutor prev = this.scrapeExecutorMap.put(x.getWebsiteName(), x);
            if (prev != null) {
                throw new RuntimeException("Internal error, more then one ScrapeExecutor have the same websiteName: " + x.getWebsiteName());
            }
        });
    }

    @PostConstruct
    public void rescheduleProductTrackings() {
        List<RegisteredProductTracking> trackingList = registeredProductTrackingRepository.findAll();
        for (RegisteredProductTracking tracking : trackingList) {
            Runnable runnable = getProductTrackingRunnable(tracking.getId());

            // Restart each schedule after 1 minute after the startup
            ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleWithFixedDelay(runnable,
                    Instant.now().plusSeconds(60),
                    Duration.ofMillis(tracking.getIntervalDuration()));

            currentScheduledProductTracking.put(tracking.getId(), scheduledFuture);
            logger.info("Rescheduled tracking webhook successfully with id : {}!", tracking.getId());
        }
    }

    @Override
    public <T> T replicateByTrackingId(String trackingId) throws HttpStatusException, UnsuccessfulScrapeException {
        ProductTracking productTracking = productTrackingRepository.findById(trackingId)
                .orElseThrow(EntityNotFoundException::new);

        return replicateScrapeAction(productTracking.getUrl(), productTracking.getWebsiteName(), productTracking.getAction());
    }

    @Override
    public <T> T replicateScrapeAction(String url, WebsiteName websiteName, TrackingAction trackingAction) throws HttpStatusException, UnsuccessfulScrapeException {
        return scrapeExecutorMap.get(websiteName).executeScrapeAction(trackingAction, url);
    }

    @Transactional
    @Override
    public void scheduleTrackingListening(final String trackingId, final String url, final long intervalMs, final DQueryRequestWebhook filters) throws TrackingAlreadyScheduledException, TrackingExpiredException {
        if (StringUtils.isBlank(url)) {
            // check later for the spring exception used
            // check later with regex if it's an url
            throw new RuntimeException("Url must be provided");
        }
        if (intervalMs <= 0) {
            throw new RuntimeException("Internal error, interval is less or equal to 0");
        }
        if (currentScheduledProductTracking.containsKey(trackingId)) {
            throw new TrackingAlreadyScheduledException(format("Request of tracking schedule but Tracking id: %s was already scheduled!", trackingId));
        }

        ProductTracking productTracking = productTrackingRepository.findById(trackingId)
                .orElseThrow(() -> new EntityNotFoundException(format("Schedule requested but the Tracking id: %s no longed exists!", trackingId)));
        if (productTracking.getRegisteredProductTracking() != null) {
            throw new TrackingAlreadyScheduledException(format("Request of tracking schedule but Tracking id: %s was already scheduled!", trackingId));
        }
        if (productTracking.getWebhookRegisterExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TrackingExpiredException(format("Request of tracking schedule Tracking id: %s failed because it expired in %s",
                    trackingId, productTracking.getWebhookRegisterExpiresAt()));
        }

        RegisteredProductTracking registeredTracking = new RegisteredProductTracking();
        registeredTracking.setId(productTracking.getId());
        registeredTracking.setWebhookUrl(url);
        registeredTracking.setFilters(filters);
        registeredTracking.setIntervalDuration(intervalMs);
        registeredTracking.setProductTracking(productTracking);
        registeredTracking = registeredProductTrackingRepository.save(registeredTracking);

        Runnable runnable = getProductTrackingRunnable(trackingId);
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleWithFixedDelay(runnable,
                Instant.now().plusMillis(registeredTracking.getIntervalDuration()),
                Duration.ofMillis(registeredTracking.getIntervalDuration()));

        currentScheduledProductTracking.put(trackingId, scheduledFuture);
        logger.info("Registered tracking webhook successfully with id : {}!", trackingId);
    }

    @Transactional
    @Override
    public void unscheduleTrackingListening(String trackingId) {
        if (!currentScheduledProductTracking.containsKey(trackingId)) {
            return;
        }

        ProductTracking tracking = productTrackingRepository.findById(trackingId).orElseThrow(
                () -> new EntityNotFoundException(format("Tracking with id: %s no longer exists!", trackingId)));
        tracking.setRegisteredProductTracking(null);
        tracking.setWebhookRegisterExpiresAt(LocalDateTime.now().plusMinutes(defaultRegisteredExpireInMinutes));
        productTrackingRepository.save(tracking);

        ScheduledFuture<?> scheduledFuture = currentScheduledProductTracking.remove(trackingId);
        scheduledFuture.cancel(false);
        logger.info("Unregistered tracking webhook successfully with id : {}!", trackingId);
    }

    private Runnable getProductTrackingRunnable(String trackingId) {
        return () -> {
            Optional<ProductTracking> trackingOptional = productTrackingRepository.findById(trackingId);
            if (trackingOptional.isEmpty()) {
                logger.error("Tracking with id: {} no longer exists!", trackingId);
                return;
            }

            ProductTracking tracking = trackingOptional.get();
            RegisteredProductTracking registeredTracking = tracking.getRegisteredProductTracking();
            if (registeredTracking == null) {
                logger.error("Tracking with id: {} has no registered webhook!", trackingId);
                return;
            }

            Object scrapedResult;
            try {
                scrapedResult = replicateScrapeAction(tracking.getUrl(), tracking.getWebsiteName(), tracking.getAction());
                logger.info("Successfully scraped product with tracking id: {}", trackingId);

                boolean returnsList = tracking.getAction().isReturnsList();
                boolean hasFilters = registeredTracking.getFilters() != null;
                if (returnsList && hasFilters) {
                    List<? extends DQueriable> scrapedList = (List<? extends DQueriable>) scrapedResult;
                    registeredTracking.getFilters().filterQueriables(scrapedList);
                    logger.info("Filtered scheduled scraped list with tracking id: {}", trackingId);
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
                                logger.info("One of the two lists are empty/null!");
                                prevDataChanged = true;
                            } else if (convertedPrevData.size() != scrapedList.size()) {
                                logger.info("Lists have different sizes!");
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
                            logger.info("Previous data was equal to the current one scraped, webhook will not be called due to filters!");
                            return;
                        }

                    } else {
                        prevDataChanged = true;
                    }
                }

                if (prevDataChanged) {
                    registeredTracking.setLastScrapedDataJson(AppUtils.toJson(scrapedResult));
                    registeredProductTrackingRepository.save(registeredTracking);
                }

                String resultTest = AppUtils.toJson(scrapedResult);
                logger.info(resultTest);

                makeWebhookRequest(trackingId, registeredTracking.getWebhookUrl(), scrapedResult);
            } catch (HttpStatusException | UnsuccessfulScrapeException e) {
                logger.error(e.getMessage());
            } catch (JsonProcessingException e) {
                logger.error("Logging test result failed: {}", e.getMessage());
            }
        };
    }

    private void makeWebhookRequest(String trackingId, String webhook, Object body) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.postForLocation(URI.create(webhook), WebhookScrapeInput.builder().result(body).trackingId(trackingId).build());
            logger.info("Successfully scraped product with tracking id: {} and sent via webhook", trackingId);
        } catch (RestClientException ex) {
            // implement max retries before deleting the schedule
            logger.info("Failed to send scraped result via webhook (trackingId: {}, webhookUrl: {}, message: {})",
                    trackingId, webhook, ex.getMessage());
        }
    }
}
