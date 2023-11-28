package com.paperized.shopapi.service;


import com.paperized.generated.shopapi.model.ProductTrackingDto;
import com.paperized.shopapi.model.TrackingAction;

public interface TrackingService {
    ProductTrackingDto generateNewTracking(String url, TrackingAction action);
}
