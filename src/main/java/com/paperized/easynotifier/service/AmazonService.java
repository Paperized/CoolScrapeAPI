package com.paperized.easynotifier.service;

import com.paperized.easynotifier.dto.TrackListeningDto;
import com.paperized.generated.easynotifier.model.AmazonProductDto;
import com.paperized.generated.easynotifier.model.AmazonProductTracked;
import com.paperized.easynotifier.model.webhookfilter.DQueryRequestScheduled;
import com.paperized.easynotifier.exceptions.UnsuccessfulScrapeException;
import org.jsoup.HttpStatusException;

public interface AmazonService {
    AmazonProductTracked findProductDetailsTracked(String url, TrackListeningDto trackListeningDto) throws HttpStatusException, UnsuccessfulScrapeException;
    AmazonProductDto findProductDetails(String url) throws HttpStatusException, UnsuccessfulScrapeException;
}
