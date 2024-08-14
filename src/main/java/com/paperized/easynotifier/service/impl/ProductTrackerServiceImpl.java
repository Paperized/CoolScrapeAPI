package com.paperized.easynotifier.service.impl;

import com.paperized.easynotifier.dto.TrackListeningDto;
import com.paperized.easynotifier.model.webhookfilter.DQueryRequestScheduled;
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
    public String trackNewProduct(String url, WebsiteName websiteName, TrackerAction action, TrackListeningDto trackListeningDto) {
        String productTrackerId = UUID.randomUUID().toString();

        ProductTracker productTracker = new ProductTracker();
        productTracker.setId(productTrackerId);
        productTracker.setUrl(url);
        productTracker.setAction(action);
        productTracker.setWebsiteName(websiteName);

        ProductTrackerDetails productTrackerDetails = new ProductTrackerDetails();
        productTrackerDetails.setId(productTrackerId);
        productTrackerDetails.setWebhookUrl(trackListeningDto.getWebhookUrl());
        productTrackerDetails.setWsEnabled(trackListeningDto.isWsEnabled());
        productTrackerDetails.setFilters(trackListeningDto.getDQueryRequestScheduled());
        productTrackerDetails.setIntervalDuration(1000);
        productTracker.setProductTrackerDetails(productTrackerDetails);

        productTrackerRepository.save(productTracker);
        return productTrackerId;
    }

    @Override
    public boolean existsTrackerIdWithWS(String trackerId) {
        return productTrackerRepository.existsTrackerIdWithWsEnabled(trackerId);
    }
}
