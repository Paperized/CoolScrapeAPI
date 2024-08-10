package com.paperized.easynotifier.service.impl;

import com.paperized.generated.easynotifier.model.AmazonProductDto;
import com.paperized.generated.easynotifier.model.AmazonProductTracked;
import com.paperized.generated.easynotifier.model.TrackerInfoDto;
import com.paperized.easynotifier.model.webhookfilter.DQueryRequestWebhook;
import com.paperized.easynotifier.exceptions.TrackingAlreadyScheduledException;
import com.paperized.easynotifier.exceptions.UnsuccessfulScrapeException;
import com.paperized.easynotifier.model.TrackerAction;
import com.paperized.easynotifier.model.WebsiteName;
import com.paperized.easynotifier.scraper.AmazonScraper;
import com.paperized.easynotifier.service.AmazonService;
import com.paperized.easynotifier.service.ProductTrackerScheduler;
import com.paperized.easynotifier.service.ProductTrackerService;
import org.jsoup.HttpStatusException;
import org.springframework.stereotype.Service;

@Service
public class AmazonServiceImpl implements AmazonService {
    private final AmazonScraper amazonScraper;
    private final ProductTrackerService productTrackerService;
    private final ProductTrackerScheduler productTrackerScheduler;

    public AmazonServiceImpl(AmazonScraper amazonScraper, ProductTrackerService productTrackerService, ProductTrackerScheduler productTrackerScheduler) {
        this.amazonScraper = amazonScraper;
        this.productTrackerService = productTrackerService;
        this.productTrackerScheduler = productTrackerScheduler;
    }

    @Override
    public AmazonProductTracked findProductDetailsTracked(String url, String webhookUrl, DQueryRequestWebhook queryRequestWebhook) throws HttpStatusException, UnsuccessfulScrapeException {
        AmazonProductDto amazonProduct = findProductDetails(url);
        String trackerId = productTrackerService.trackNewProduct(url, WebsiteName.Amazon, TrackerAction.AMAZON_PRODUCT_DETAILS, webhookUrl, queryRequestWebhook);

        try {
            productTrackerScheduler.scheduleTracker(trackerId, true);
        } catch (TrackingAlreadyScheduledException e) {
            throw new RuntimeException("Inconsistency, Created Tracker with id " + trackerId + " but it's already defined in the in-memory scheduler");
        }

        return new AmazonProductTracked()
                .item(amazonProduct)
                .track(new TrackerInfoDto().trackerId(trackerId));
    }

    @Override
    public AmazonProductDto findProductDetails(String url) throws HttpStatusException, UnsuccessfulScrapeException {
        return amazonScraper.findProductDetails(url);
    }
}
