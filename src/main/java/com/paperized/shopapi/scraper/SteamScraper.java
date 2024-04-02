package com.paperized.shopapi.scraper;

import com.paperized.generated.shopapi.model.SteamProfileDto;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import org.jsoup.HttpStatusException;

public interface SteamScraper {
    SteamProfileDto findSteamProfile(String url) throws HttpStatusException, UnsuccessfulScrapeException;
}
