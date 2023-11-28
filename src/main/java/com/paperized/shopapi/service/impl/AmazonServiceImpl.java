package com.paperized.shopapi.service.impl;

import com.paperized.generated.shopapi.model.AmazonProductDto;
import com.paperized.generated.shopapi.model.AmazonProductTracked;
import com.paperized.generated.shopapi.model.ProductTrackingDto;
import com.paperized.shopapi.model.TrackingAction;
import com.paperized.shopapi.scraper.AmazonScraper;
import com.paperized.shopapi.service.AmazonService;
import com.paperized.shopapi.service.TrackingService;
import org.jsoup.HttpStatusException;
import org.springframework.stereotype.Service;

@Service
public class AmazonServiceImpl implements AmazonService {
    private final AmazonScraper amazonScraper;
    private final TrackingService trackingService;

    public AmazonServiceImpl(AmazonScraper amazonScraper, TrackingService trackingService) {
        this.amazonScraper = amazonScraper;
        this.trackingService = trackingService;
    }

    @Override
    public AmazonProductTracked findProductDetails(String url) throws HttpStatusException {
        AmazonProductDto amazonProduct = amazonScraper.findProductDetails(url);
        ProductTrackingDto tracking = trackingService.generateNewTracking(url, TrackingAction.AMAZON_PRODUCT_DETAILS);

        return new AmazonProductTracked()
                .item(amazonProduct)
                .track(tracking);
    }
}
