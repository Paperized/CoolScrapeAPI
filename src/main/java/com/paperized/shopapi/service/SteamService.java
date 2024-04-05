package com.paperized.shopapi.service;

import com.paperized.generated.shopapi.model.SteamProfileDto;
import com.paperized.generated.shopapi.model.SteamProfileTracked;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import org.jsoup.HttpStatusException;

public interface SteamService {
    SteamProfileTracked findSteamProfileTracked(String url) throws HttpStatusException, UnsuccessfulScrapeException;
    SteamProfileDto findSteamProfile(String url) throws HttpStatusException, UnsuccessfulScrapeException;
}
