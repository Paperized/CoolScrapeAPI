package com.paperized.easynotifier.service;

import com.paperized.generated.easynotifier.model.SteamProfileDto;
import com.paperized.generated.easynotifier.model.SteamProfileTracked;
import com.paperized.easynotifier.model.webhookfilter.DQueryRequestWebhook;
import com.paperized.easynotifier.exceptions.UnsuccessfulScrapeException;
import org.jsoup.HttpStatusException;

public interface SteamService {
    SteamProfileTracked findSteamProfileTracked(String url, String webhookUrl, DQueryRequestWebhook queryRequestWebhook) throws HttpStatusException, UnsuccessfulScrapeException;
    SteamProfileDto findSteamProfile(String url) throws HttpStatusException, UnsuccessfulScrapeException;
}
