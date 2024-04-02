package com.paperized.shopapi.service.impl;

import com.paperized.generated.shopapi.model.*;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import com.paperized.shopapi.model.TrackingAction;
import com.paperized.shopapi.model.WebsiteName;
import com.paperized.shopapi.scraper.AmazonScraper;
import com.paperized.shopapi.scraper.SteamScraper;
import com.paperized.shopapi.service.AmazonService;
import com.paperized.shopapi.service.SteamService;
import com.paperized.shopapi.service.TrackingService;
import org.jsoup.HttpStatusException;
import org.springframework.stereotype.Service;

@Service
public class SteamServiceImpl implements SteamService {
    private final SteamScraper steamScraper;
    private final TrackingService trackingService;

    public SteamServiceImpl(SteamScraper steamScraper, TrackingService trackingService) {
        this.steamScraper = steamScraper;
        this.trackingService = trackingService;
    }

    @Override
    public SteamProfileTracked findSteamProfile(String url) throws HttpStatusException, UnsuccessfulScrapeException {
        SteamProfileDto steamProfile = steamScraper.findSteamProfile(url);
        ProductTrackingDto tracking = trackingService.generateNewTracking(url, WebsiteName.Steam, TrackingAction.STEAM_FIND_PROFILE);

        return new SteamProfileTracked()
                .item(steamProfile)
                .track(tracking);
    }
}
