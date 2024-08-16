package com.paperized.easynotifier.scraper;

import com.paperized.easynotifier.exceptions.UnsuccessfulScrapeException;
import com.paperized.generated.easynotifier.model.LinkedinCandidateDto;
import org.jsoup.HttpStatusException;

import java.util.List;

public interface LinkedinScraper {
    List<LinkedinCandidateDto> findCandidates(String url) throws HttpStatusException, UnsuccessfulScrapeException;
}
