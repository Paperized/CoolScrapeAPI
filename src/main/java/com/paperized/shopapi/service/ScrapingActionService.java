package com.paperized.shopapi.service;

import com.paperized.shopapi.model.TrackingAction;
import com.paperized.shopapi.model.WebsiteName;
import org.jsoup.HttpStatusException;

public interface ScrapingActionService {
    <T> T replicateByTrackingId(String trackingId) throws HttpStatusException;
    <T> T replicateScrapeAction(String url, WebsiteName websiteName, TrackingAction trackingAction) throws HttpStatusException;
}
