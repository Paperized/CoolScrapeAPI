package com.paperized.easynotifier.service;

import com.paperized.easynotifier.dto.TrackListeningDto;
import com.paperized.generated.easynotifier.model.SteamProfileDto;
import com.paperized.generated.easynotifier.model.SteamProfileTracked;
import com.paperized.easynotifier.model.webhookfilter.DQueryRequestScheduled;
import com.paperized.easynotifier.exceptions.UnsuccessfulScrapeException;
import org.jsoup.HttpStatusException;

public interface SteamService {
    SteamProfileTracked findSteamProfileTracked(String url, TrackListeningDto trackListeningDto) throws HttpStatusException, UnsuccessfulScrapeException;
    SteamProfileDto findSteamProfile(String url) throws HttpStatusException, UnsuccessfulScrapeException;
}
