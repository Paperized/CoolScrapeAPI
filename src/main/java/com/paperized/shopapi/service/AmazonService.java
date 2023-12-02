package com.paperized.shopapi.service;

import com.paperized.generated.shopapi.model.AmazonProductTracked;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import org.jsoup.HttpStatusException;

public interface AmazonService {
    AmazonProductTracked findProductDetails(String url) throws HttpStatusException, UnsuccessfulScrapeException;
}
