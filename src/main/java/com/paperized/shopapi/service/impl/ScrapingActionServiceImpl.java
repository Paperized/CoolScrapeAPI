package com.paperized.shopapi.service.impl;

import com.paperized.shopapi.exceptions.TrackingExpiredException;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import com.paperized.shopapi.model.ProductTracking;
import com.paperized.shopapi.model.TrackingAction;
import com.paperized.shopapi.model.WebsiteName;
import com.paperized.shopapi.repository.ProductTrackingRepository;
import com.paperized.shopapi.scraper.ScrapeExecutor;
import com.paperized.shopapi.service.ScrapingActionService;
import jakarta.persistence.EntityNotFoundException;
import org.jsoup.HttpStatusException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScrapingActionServiceImpl implements ScrapingActionService {
    private final Map<WebsiteName, ScrapeExecutor> scrapeExecutorMap;
    private final ProductTrackingRepository productTrackingRepository;

    public ScrapingActionServiceImpl(List<ScrapeExecutor> scrapeExecutors, ProductTrackingRepository productTrackingRepository) {
        this.productTrackingRepository = productTrackingRepository;
        this.scrapeExecutorMap = new HashMap<>();
        scrapeExecutors.forEach(x -> {
            ScrapeExecutor prev = this.scrapeExecutorMap.put(x.getWebsiteName(), x);
            if(prev != null) {
                throw new RuntimeException("Internal error, more then one ScrapeExecutor have the same websiteName: " + x.getWebsiteName());
            }
        });
    }

    @Override
    public <T> T replicateByTrackingId(String trackingId) throws HttpStatusException, UnsuccessfulScrapeException {
        ProductTracking tracking = productTrackingRepository.findById(trackingId).orElseThrow(EntityNotFoundException::new);
        if(tracking.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new TrackingExpiredException("Tracking expired at " + tracking.getExpiresAt());
        }

        return replicateScrapeAction(tracking.getUrl(), tracking.getWebsiteName(), tracking.getAction());
    }

    @Override
    public <T> T replicateScrapeAction(String url, WebsiteName websiteName, TrackingAction trackingAction) throws HttpStatusException, UnsuccessfulScrapeException {
        return scrapeExecutorMap.get(websiteName).executeScrapeAction(trackingAction, url);
    }

}
