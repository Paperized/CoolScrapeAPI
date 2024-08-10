package com.paperized.easynotifier.scraper;

import com.paperized.easynotifier.dto.WebsiteSetting;
import com.paperized.easynotifier.exceptions.ScraperFailedConnectionException;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;

public interface ScraperHttpService {
    Document getPage(String url) throws ScraperFailedConnectionException;
    Document getPage(String url, WebsiteSetting websiteSetting) throws ScraperFailedConnectionException, HttpStatusException;
}
