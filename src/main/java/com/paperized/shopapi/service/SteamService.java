package com.paperized.shopapi.service;

import com.paperized.generated.shopapi.model.SteamProfileTracked;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import org.jsoup.HttpStatusException;

public interface SteamService {
    SteamProfileTracked findSteamProfile(String url) throws HttpStatusException, UnsuccessfulScrapeException;
}
