package com.paperized.shopapi.service.impl;

import com.paperized.shopapi.dto.DQueryRequestWebhook;
import com.paperized.shopapi.model.ProductTracker;
import com.paperized.shopapi.model.ProductTrackerDetails;
import com.paperized.shopapi.model.TrackerAction;
import com.paperized.shopapi.model.WebsiteName;
import com.paperized.shopapi.repository.ProductTrackerRepository;
import com.paperized.shopapi.service.ProductTrackerService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductTrackerServiceImpl implements ProductTrackerService {
    private final ProductTrackerRepository productTrackerRepository;

    public ProductTrackerServiceImpl(ProductTrackerRepository productTrackerRepository) {
        this.productTrackerRepository = productTrackerRepository;
    }

    @Override
    public String trackNewProduct(String url, WebsiteName websiteName, TrackerAction action, String webhookUrl, DQueryRequestWebhook queryRequestWebhook) {
        String productTrackerId = UUID.randomUUID().toString();

        ProductTracker productTracker = new ProductTracker();
        productTracker.setId(productTrackerId);
        productTracker.setUrl(url);
        productTracker.setAction(action);
        productTracker.setWebsiteName(websiteName);

        ProductTrackerDetails productTrackerDetails = new ProductTrackerDetails();
        productTrackerDetails.setId(productTrackerId);
        productTrackerDetails.setWebhookUrl(webhookUrl);
        productTrackerDetails.setFilters(queryRequestWebhook);
        productTrackerDetails.setIntervalDuration(1000);
        productTracker.setProductTrackerDetails(productTrackerDetails);

        productTrackerRepository.save(productTracker);
        return productTrackerId;
    }
}
