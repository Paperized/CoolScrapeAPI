package com.paperized.easynotifier.service.impl;

import com.paperized.generated.easynotifier.model.SteamProfileDto;
import com.paperized.generated.easynotifier.model.SteamProfileTracked;
import com.paperized.generated.easynotifier.model.TrackerInfoDto;
import com.paperized.easynotifier.model.webhookfilter.DQueryRequestWebhook;
import com.paperized.easynotifier.exceptions.TrackingAlreadyScheduledException;
import com.paperized.easynotifier.exceptions.UnsuccessfulScrapeException;
import com.paperized.easynotifier.model.TrackerAction;
import com.paperized.easynotifier.model.WebsiteName;
import com.paperized.easynotifier.scraper.SteamScraper;
import com.paperized.easynotifier.service.ProductTrackerScheduler;
import com.paperized.easynotifier.service.ProductTrackerService;
import com.paperized.easynotifier.service.SteamService;
import org.jsoup.HttpStatusException;
import org.springframework.stereotype.Service;

@Service
public class SteamServiceImpl implements SteamService {
    private final SteamScraper steamScraper;
    private final ProductTrackerService productTrackerService;
    private final ProductTrackerScheduler productTrackerScheduler;

    public SteamServiceImpl(SteamScraper steamScraper, ProductTrackerService productTrackerService, ProductTrackerScheduler productTrackerScheduler) {
        this.steamScraper = steamScraper;
        this.productTrackerService = productTrackerService;
        this.productTrackerScheduler = productTrackerScheduler;
    }

    @Override
    public SteamProfileTracked findSteamProfileTracked(String url, String webhookUrl, DQueryRequestWebhook queryRequestWebhook) throws HttpStatusException, UnsuccessfulScrapeException {
        SteamProfileDto steamProfile = findSteamProfile(url);
        String trackerId = productTrackerService.trackNewProduct(url, WebsiteName.Steam, TrackerAction.STEAM_FIND_PROFILE, webhookUrl, queryRequestWebhook);

        try {
            productTrackerScheduler.scheduleTracker(trackerId, true);
        } catch (TrackingAlreadyScheduledException e) {
            throw new RuntimeException("Inconsistency, Created Tracker with id " + trackerId + " but it's already defined in the in-memory scheduler");
        }

        return new SteamProfileTracked()
                .item(steamProfile)
                .track(new TrackerInfoDto().trackerId(trackerId));
    }

    @Override
    public SteamProfileDto findSteamProfile(String url) throws HttpStatusException, UnsuccessfulScrapeException {
        return steamScraper.findSteamProfile(url);
    }
}
