package com.paperized.easynotifier.service.impl;

import com.paperized.easynotifier.exceptions.UnsuccessfulScrapeException;
import com.paperized.easynotifier.model.ProductTracker;
import com.paperized.easynotifier.model.TrackerAction;
import com.paperized.easynotifier.model.WebsiteName;
import com.paperized.easynotifier.repository.ProductTrackerRepository;
import com.paperized.easynotifier.scraper.ScrapeExecutor;
import com.paperized.easynotifier.service.ScrapingActionService;
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
