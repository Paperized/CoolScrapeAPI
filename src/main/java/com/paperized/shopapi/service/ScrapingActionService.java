package com.paperized.shopapi.service;

import com.paperized.shopapi.exceptions.TrackingAlreadyScheduledException;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import com.paperized.shopapi.model.TrackingAction;
import com.paperized.shopapi.model.WebsiteName;
import org.jsoup.HttpStatusException;

public interface ScrapingActionService {
    <T> T replicateByTrackingId(String trackingId) throws HttpStatusException, UnsuccessfulScrapeException;
    <T> T replicateScrapeAction(String url, WebsiteName websiteName, TrackingAction trackingAction) throws HttpStatusException, UnsuccessfulScrapeException;
    void scheduleTrackingListening(final String trackingId, final String url, final long intervalMs) throws TrackingAlreadyScheduledException;
    void unscheduleTrackingListening(String trackingId);
}
