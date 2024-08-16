package com.paperized.easynotifier.scraper.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.paperized.easynotifier.config.ScraperSettings;
import com.paperized.easynotifier.exceptions.UnsuccessfulScrapeException;
import com.paperized.easynotifier.model.TrackerAction;
import com.paperized.easynotifier.model.WebsiteName;
import com.paperized.easynotifier.scraper.LinkedinScraper;
import com.paperized.easynotifier.scraper.ScrapeExecutor;
import com.paperized.easynotifier.scraper.ScraperHttpService;
import com.paperized.easynotifier.scraper.annotations.ScrapeAction;
import com.paperized.easynotifier.utils.AppUtils;
import com.paperized.easynotifier.utils.ScraperUtils;
import com.paperized.generated.easynotifier.model.LinkedinCandidateDto;
import com.paperized.generated.easynotifier.model.SteamProfileDto;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.springframework.data.web.JsonPath;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LinkedinScraperImpl extends ScrapeExecutor implements LinkedinScraper {
    private final ScraperHttpService scraperHttpService;

    public LinkedinScraperImpl(ScraperHttpService scraperHttpService, ScraperSettings scraperSettings) {
        super(scraperSettings, WebsiteName.Linkedin);
        this.scraperHttpService = scraperHttpService;
    }

    @Override
    public List<LinkedinCandidateDto> findCandidates(String url) throws HttpStatusException, UnsuccessfulScrapeException {
        return executeScrapeAction(TrackerAction.LINKEDIN_FIND_CANDIDATES, url);
    }

    @ScrapeAction(action = TrackerAction.LINKEDIN_FIND_CANDIDATES, retryTimes = 1, intervalRetry = 1000)
    private List<LinkedinCandidateDto> findCandidatesInternal(String url) throws HttpStatusException, UnsuccessfulScrapeException {
        var response = scraperHttpService.getAnyContentPage(url);
        JsonNode responseNode = convertResponseToJson(response.body());

        List<LinkedinCandidateDto> output = new ArrayList<>();

        ArrayNode candidateNode = ScraperUtils.getNodeAndThrow(
                () -> (ArrayNode) responseNode.get("included"), "Invalid JSON text");
        for(JsonNode potentialCandidate : candidateNode) {
            String templateType = ScraperUtils.getData(() -> potentialCandidate.get("template").asText());
            if(!StringUtils.equalsIgnoreCase(templateType, "UNIVERSAL")) continue;

            String candidateId = ScraperUtils.getData(() -> potentialCandidate.get("trackingUrn").asText(), "Type UNIVERSAL but no trackingUrn");
            if(StringUtils.isBlank(candidateId)) continue;

            Integer candidateDistance = ScraperUtils.getData(() -> {
                String text = potentialCandidate.at("entityCustomTrackingInfo/memberDistance").asText();
                return Integer.parseInt(text.split("_")[1]);
            });

            // continua con la risposta di
            // https://www.linkedin.com/voyager/api/graphql?variables=(start:0,count:20,origin:OTHER,query:(keywords:software%20engineer%20AND%20Angular,flagshipSearchIntent:SEARCH_SRP,queryParameters:List((key:resultType,value:List(PEOPLE))),includeFiltersInResponse:false))&queryId=voyagerSearchDashClusters.37920f17209f22c510dd410658abc540
        }

        return output;
    }
}
