package com.paperized.easynotifier.service.impl;

import com.paperized.easynotifier.model.webhookfilter.DQueryRequestWebhook;
import com.paperized.easynotifier.model.ProductTracker;
import com.paperized.easynotifier.model.ProductTrackerDetails;
import com.paperized.easynotifier.model.TrackerAction;
import com.paperized.easynotifier.model.WebsiteName;
import com.paperized.easynotifier.repository.ProductTrackerRepository;
import com.paperized.easynotifier.service.ProductTrackerService;
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
