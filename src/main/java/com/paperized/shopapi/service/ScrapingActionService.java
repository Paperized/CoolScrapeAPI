package com.paperized.shopapi.service;

import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import com.paperized.shopapi.model.TrackerAction;
import com.paperized.shopapi.model.WebsiteName;
import org.jsoup.HttpStatusException;

public interface ScrapingActionService {
    <T> T replicateByTrackingId(String trackerId) throws HttpStatusException, UnsuccessfulScrapeException;
    <T> T replicateScrapeAction(String url, WebsiteName websiteName, TrackerAction trackerAction) throws HttpStatusException, UnsuccessfulScrapeException;
}
