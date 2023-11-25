package com.paperized.shopapi.scraper;

import com.paperized.shopapi.dto.WebsiteSetting;
import com.paperized.shopapi.exceptions.ScraperFailedConnectionException;
import org.jsoup.nodes.Document;

public interface ScraperHttpService {
    Document getPage(String url) throws ScraperFailedConnectionException;
    Document getPage(String url, WebsiteSetting websiteSetting) throws ScraperFailedConnectionException;
}
