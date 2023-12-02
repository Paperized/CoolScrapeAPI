package com.paperized.shopapi.service.impl;

import com.paperized.generated.shopapi.model.ProductTrackingDto;
import com.paperized.shopapi.model.ProductTracking;
import com.paperized.shopapi.model.TrackingAction;
import com.paperized.shopapi.model.WebsiteName;
import com.paperized.shopapi.repository.ProductTrackingRepository;
import com.paperized.shopapi.service.TrackingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TrackingServiceImpl implements TrackingService {
    private final Long defaultRegistredExpireInMinutes;
    private final ProductTrackingRepository productTrackingRepository;

    public TrackingServiceImpl(@Value("${tracking.defaultRegistrationExpireTime}") Long defaultRegistredExpireInMinutes, ProductTrackingRepository productTrackingRepository) {
        this.defaultRegistredExpireInMinutes = defaultRegistredExpireInMinutes;
        this.productTrackingRepository = productTrackingRepository;
    }

    @Override
    public ProductTrackingDto generateNewTracking(String url, WebsiteName websiteName, TrackingAction action) {
        ProductTracking productTracking = new ProductTracking();
        productTracking.setId(UUID.randomUUID().toString());
        productTracking.setWebhookRegisterExpiresAt(LocalDateTime.now().plusMinutes(defaultRegistredExpireInMinutes));
        productTracking.setUrl(url);
        productTracking.setAction(action);
        productTracking.setWebsiteName(websiteName);

        ProductTracking savedTracking = productTrackingRepository.save(productTracking);
        return new ProductTrackingDto()
                .id(savedTracking.getId())
                .webhookRegisterExpiresAt(savedTracking.getWebhookRegisterExpiresAt());
    }
}
