package com.paperized.easynotifier.controller;

import com.paperized.generated.easynotifier.api.TrackingApi;
import com.paperized.easynotifier.service.ProductTrackerScheduler;
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
