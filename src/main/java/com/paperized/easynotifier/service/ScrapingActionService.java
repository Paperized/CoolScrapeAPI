package com.paperized.easynotifier.service;

import com.paperized.easynotifier.exceptions.UnsuccessfulScrapeException;
import com.paperized.easynotifier.model.TrackerAction;
import com.paperized.easynotifier.model.WebsiteName;
import org.jsoup.HttpStatusException;

public interface ScrapingActionService {
    <T> T replicateByTrackingId(String trackerId) throws HttpStatusException, UnsuccessfulScrapeException;
    <T> T replicateScrapeAction(String url, WebsiteName websiteName, TrackerAction trackerAction) throws HttpStatusException, UnsuccessfulScrapeException;
}
