package com.paperized.shopapi.scraper.impl;

import com.paperized.shopapi.dto.WebsiteSetting;
import com.paperized.shopapi.exceptions.ScraperFailedConnectionException;
import com.paperized.shopapi.scraper.ScraperHttpService;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class ScraperHttpServiceImpl implements ScraperHttpService {
    private static final Random random = new Random();
    // manages proxy

    @Override
    public Document getPage(String url) throws ScraperFailedConnectionException {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new ScraperFailedConnectionException("Error while downloading page", e);
        }
    }

    @Override
    public Document getPage(String url, WebsiteSetting websiteSetting) throws ScraperFailedConnectionException, HttpStatusException {
        try {
            return Jsoup
                    .connect(url)
                    .userAgent(getRandomUserAgent(websiteSetting))
                    .headers(emptyHeadersIfNull(websiteSetting.getExtraHeaders()))
                    .get();
        } catch (HttpStatusException e) {
            throw e;
        }catch (IOException e) {
            throw new ScraperFailedConnectionException("Error while downloading page", e);
        }
    }

    private static String getRandomUserAgent(WebsiteSetting websiteSetting) {
        List<String> availableHeaders = websiteSetting.getAvailableHeaders();
        if(CollectionUtils.isEmpty(availableHeaders)) {
            return "";
        }

        return availableHeaders.get(random.nextInt(availableHeaders.size()));
    }

    private static Map<String, String> emptyHeadersIfNull(Map<String, String> headers) {
        if(headers == null)
            return Map.of();
        return headers;
    }
}
