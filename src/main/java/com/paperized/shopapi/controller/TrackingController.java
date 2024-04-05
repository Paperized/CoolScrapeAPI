package com.paperized.shopapi.controller;

import com.paperized.generated.shopapi.api.TrackingApi;
import com.paperized.shopapi.service.ScrapingActionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrackingController implements TrackingApi {
    private final ScrapingActionService scrapingActionService;

    public TrackingController(ScrapingActionService scrapingActionService) {
        this.scrapingActionService = scrapingActionService;
    }

    @Override
    public ResponseEntity<Void> unlistenTrackingWebhook(String trackingId) throws Exception {
        scrapingActionService.unscheduleTrackingListening(trackingId);
        return ResponseEntity.ok().build();
    }
}
