package com.paperized.shopapi.scraper.impl;

import com.paperized.generated.shopapi.model.SteamProfileDto;
import com.paperized.shopapi.config.ScraperSettings;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import com.paperized.shopapi.model.TrackerAction;
import com.paperized.shopapi.model.WebsiteName;
import com.paperized.shopapi.scraper.ScrapeExecutor;
import com.paperized.shopapi.scraper.ScraperHttpService;
import com.paperized.shopapi.scraper.SteamScraper;
import com.paperized.shopapi.scraper.annotations.ScrapeAction;
import com.paperized.shopapi.utils.ScraperUtils;
import io.micrometer.common.util.StringUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
public class SteamScraperImpl extends ScrapeExecutor implements SteamScraper {
    private final ScraperHttpService scraperHttpService;

    public SteamScraperImpl(ScraperHttpService scraperHttpService, ScraperSettings scraperSettings) {
        super(scraperSettings, WebsiteName.Steam);
        this.scraperHttpService = scraperHttpService;
    }

    @Override
    public SteamProfileDto findSteamProfile(String url) throws HttpStatusException, UnsuccessfulScrapeException {
        return executeScrapeAction(TrackerAction.STEAM_FIND_PROFILE, url);
    }

    @ScrapeAction(action = TrackerAction.STEAM_FIND_PROFILE, retryTimes = 1, intervalRetry = 1000)
    private SteamProfileDto findSteamProfileInternal(String url) throws HttpStatusException, UnsuccessfulScrapeException {
        if(!url.startsWith(websiteSetting.getBaseUrl())) {
            throw new UnsuccessfulScrapeException("Url is not a steam recognized url!");
        }

        Document page = scraperHttpService.getPage(url, websiteSetting);

        String steamName = ScraperUtils.getText(page.selectXpath("//*[starts-with(@class, 'profile_header_centered_persona')]//*[@class='actual_persona_name']").first());
        if(StringUtils.isBlank(steamName)) {
            throw new UnsuccessfulScrapeException("Steam url profile provided is not a valid user profile!");
        }

        String status = ScraperUtils.getText(page.selectXpath("//*[@class='profile_in_game_header']").first());
        boolean isOnline = !"Offline".equalsIgnoreCase(status);

        String playingAt = ScraperUtils.getText(page.selectXpath("//*[@class='profile_in_game_name']").first());

        return new SteamProfileDto()
                .name(steamName)
                .online(isOnline)
                .playingAt(playingAt);
    }
}
