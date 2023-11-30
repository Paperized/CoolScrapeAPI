package com.paperized.shopapi.service;


import com.paperized.generated.shopapi.model.ProductTrackingDto;
import com.paperized.shopapi.model.TrackingAction;
import com.paperized.shopapi.model.WebsiteName;

public interface TrackingService {
    ProductTrackingDto generateNewTracking(String url, WebsiteName websiteName, TrackingAction action);
}
