package com.paperized.shopapi.service.impl;

import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import com.paperized.shopapi.model.ProductTracker;
import com.paperized.shopapi.model.TrackerAction;
import com.paperized.shopapi.model.WebsiteName;
import com.paperized.shopapi.repository.ProductTrackerRepository;
import com.paperized.shopapi.scraper.ScrapeExecutor;
import com.paperized.shopapi.service.ScrapingActionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ScrapingActionServiceImpl implements ScrapingActionService {
    private final Map<WebsiteName, ScrapeExecutor> scrapeExecutorMap = new HashMap<>();
    private final ProductTrackerRepository productTrackerRepository;

    public ScrapingActionServiceImpl(List<ScrapeExecutor> scrapeExecutors, ProductTrackerRepository productTrackerRepository) {
        this.productTrackerRepository = productTrackerRepository;
        scrapeExecutors.forEach(x -> {
            ScrapeExecutor prev = this.scrapeExecutorMap.put(x.getWebsiteName(), x);
            if (prev != null) {
                throw new RuntimeException("Internal error, more then one ScrapeExecutor have the same websiteName: " + x.getWebsiteName());
            }
        });
    }

    @Override
    public <T> T replicateByTrackingId(String trackingId) throws HttpStatusException, UnsuccessfulScrapeException {
        ProductTracker productTracker = productTrackerRepository.findById(trackingId)
                .orElseThrow(EntityNotFoundException::new);

        return replicateScrapeAction(productTracker.getUrl(), productTracker.getWebsiteName(), productTracker.getAction());
    }

    @Override
    public <T> T replicateScrapeAction(String url, WebsiteName websiteName, TrackerAction trackerAction) throws HttpStatusException, UnsuccessfulScrapeException {
        return scrapeExecutorMap.get(websiteName).executeScrapeAction(trackerAction, url);
    }
}
