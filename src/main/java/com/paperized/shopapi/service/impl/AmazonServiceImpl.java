package com.paperized.shopapi.service.impl;

import com.paperized.generated.shopapi.model.AmazonProductDto;
import com.paperized.generated.shopapi.model.AmazonProductTracked;
import com.paperized.generated.shopapi.model.TrackerInfoDto;
import com.paperized.shopapi.dto.DQueryRequestWebhook;
import com.paperized.shopapi.exceptions.TrackingAlreadyScheduledException;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import com.paperized.shopapi.model.TrackerAction;
import com.paperized.shopapi.model.WebsiteName;
import com.paperized.shopapi.scraper.AmazonScraper;
import com.paperized.shopapi.service.AmazonService;
import com.paperized.shopapi.service.ProductTrackerScheduler;
import com.paperized.shopapi.service.ProductTrackerService;
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
