package com.paperized.shopapi.scraper;

import com.paperized.shopapi.model.TrackingAction;
import com.paperized.shopapi.model.WebsiteName;
import org.jsoup.HttpStatusException;

public interface ScrapeExecutor {
    default <T> T redoScrapingAs(String url, TrackingAction action) throws HttpStatusException {
        return (T) redoScraping(url, action);
    }

    Object redoScraping(String url, TrackingAction action) throws HttpStatusException;
    WebsiteName getWebsiteName();
}
