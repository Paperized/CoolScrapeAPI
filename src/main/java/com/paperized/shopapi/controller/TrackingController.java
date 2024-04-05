package com.paperized.shopapi.controller;

import com.paperized.generated.shopapi.api.TrackingApi;
import com.paperized.shopapi.service.ProductTrackerScheduler;
import com.paperized.shopapi.service.ScrapingActionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrackingController implements TrackingApi {
    private final ProductTrackerScheduler productTrackerScheduler;

    public TrackingController(ProductTrackerScheduler productTrackerScheduler) {
        this.productTrackerScheduler = productTrackerScheduler;
    }

    @Override
    public ResponseEntity<Void> unlistenTrackerWebhook(String trackerId) throws Exception {
        productTrackerScheduler.unscheduleTracker(trackerId);
        return ResponseEntity.ok().build();
    }
}
