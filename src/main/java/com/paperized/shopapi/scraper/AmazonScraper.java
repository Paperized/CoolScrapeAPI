package com.paperized.shopapi.scraper;

import com.paperized.generated.shopapi.model.AmazonProductDto;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import org.jsoup.HttpStatusException;

public interface AmazonScraper {
    AmazonProductDto findProductDetails(String url) throws HttpStatusException, UnsuccessfulScrapeException;
}
