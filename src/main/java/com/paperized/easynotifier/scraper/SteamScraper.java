package com.paperized.easynotifier.scraper;

import com.paperized.generated.easynotifier.model.SteamProfileDto;
import com.paperized.easynotifier.exceptions.UnsuccessfulScrapeException;
import org.jsoup.HttpStatusException;

public interface SteamScraper {
    SteamProfileDto findSteamProfile(String url) throws HttpStatusException, UnsuccessfulScrapeException;
}
