package com.paperized.easynotifier.service.impl;

import com.paperized.easynotifier.dto.TrackListeningDto;
import com.paperized.easynotifier.exceptions.TrackingAlreadyScheduledException;
import com.paperized.easynotifier.model.TrackerAction;
import com.paperized.easynotifier.model.WebsiteName;
import com.paperized.easynotifier.scraper.LinkedinScraper;
import com.paperized.easynotifier.service.LinkedinService;
import com.paperized.easynotifier.service.ProductTrackerScheduler;
import com.paperized.easynotifier.service.ProductTrackerService;
import com.paperized.generated.easynotifier.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LinkedinServiceImpl implements LinkedinService {
    private final LinkedinScraper linkedinScraper;
    private final ProductTrackerService productTrackerService;
    private final ProductTrackerScheduler productTrackerScheduler;

    public LinkedinServiceImpl(LinkedinScraper linkedinScraper, ProductTrackerService productTrackerService, ProductTrackerScheduler productTrackerScheduler) {
        this.linkedinScraper = linkedinScraper;
        this.productTrackerService = productTrackerService;
        this.productTrackerScheduler = productTrackerScheduler;
    }

    @Override
    public List<LinkedinCandidateDto> findCandidates(FindCandidatesRequest findCandidatesRequest) {
        return linkedinScraper.findCandidates(findCandidatesRequest.getUrl());
    }

    @Override
    public LinkedinCandidatesTracked findCandidatesTracked(FindCandidatesRequest findCandidatesRequest, TrackListeningDto trackListeningDto) {
        List<LinkedinCandidateDto> firstResult = findCandidates(findCandidatesRequest);
        String trackerId = productTrackerService.trackNewProduct(findCandidatesRequest.getUrl(),
                WebsiteName.Linkedin, TrackerAction.LINKEDIN_FIND_CANDIDATES, trackListeningDto);
        try {
            productTrackerScheduler.scheduleTracker(trackerId, true);
        } catch (TrackingAlreadyScheduledException e) {
            throw new RuntimeException("Inconsistency, Created Tracker with id " + trackerId + " but it's already defined in the in-memory scheduler");
        }

        return new LinkedinCandidatesTracked()
                .items(firstResult)
                .track(new TrackerInfoDto().trackerId(trackerId));
    }
}
