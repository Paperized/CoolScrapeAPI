package com.paperized.shopapi.controller;

import com.paperized.generated.shopapi.api.TrackingApi;
import com.paperized.generated.shopapi.model.RegisterWebhookRequest;
import com.paperized.shopapi.service.ScrapingActionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrackingController implements TrackingApi {
    private final Long defaultScheduleIntervalMs;
    private final ScrapingActionService scrapingActionService;

    public TrackingController(@Value("${tracking.defaultScheduleInterval}") Long defaultScheduleIntervalMs, ScrapingActionService scrapingActionService) {
        this.defaultScheduleIntervalMs = defaultScheduleIntervalMs;
        this.scrapingActionService = scrapingActionService;
    }

    @Override
    public ResponseEntity<Object> findInformationsById(String trackingId) throws Exception {
        return ResponseEntity.ok(scrapingActionService.replicateByTrackingId(trackingId));
    }

    @Override
    public ResponseEntity<Void> registerTrackingWebhook(String trackingId, RegisterWebhookRequest registerWebhookRequest) throws Exception {
        scrapingActionService.scheduleTrackingListening(trackingId, registerWebhookRequest.getUrl(), defaultScheduleIntervalMs);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> unregisterTrackingWebhook(String trackingId) throws Exception {
        scrapingActionService.unscheduleTrackingListening(trackingId);
        return ResponseEntity.ok().build();
    }
}
