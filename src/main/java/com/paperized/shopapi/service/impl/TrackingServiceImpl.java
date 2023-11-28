package com.paperized.shopapi.service.impl;

import com.paperized.generated.shopapi.model.ProductTrackingDto;
import com.paperized.shopapi.model.ProductTracking;
import com.paperized.shopapi.model.TrackingAction;
import com.paperized.shopapi.repository.ProductTrackingRepository;
import com.paperized.shopapi.service.TrackingService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class TrackingServiceImpl implements TrackingService {
    private final ProductTrackingRepository productTrackingRepository;

    public TrackingServiceImpl(ProductTrackingRepository productTrackingRepository) {
        this.productTrackingRepository = productTrackingRepository;
    }

    @Override
    public ProductTrackingDto generateNewTracking(String url, TrackingAction action) {
        ProductTracking productTracking = new ProductTracking();
        productTracking.setId(UUID.randomUUID().toString());
        productTracking.setExpiresAt(OffsetDateTime.now().plusHours(1));
        productTracking.setUrl(url);
        productTracking.setAction(action);

        ProductTracking savedTracking = productTrackingRepository.save(productTracking);
        return new ProductTrackingDto()
                .id(savedTracking.getId())
                .expiresAt(savedTracking.getExpiresAt());
    }
}
