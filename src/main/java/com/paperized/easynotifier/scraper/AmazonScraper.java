package com.paperized.easynotifier.scraper;

import com.paperized.generated.easynotifier.model.AmazonProductDto;
import com.paperized.easynotifier.exceptions.UnsuccessfulScrapeException;
import org.jsoup.HttpStatusException;

public interface AmazonScraper {
    AmazonProductDto findProductDetails(String url) throws HttpStatusException, UnsuccessfulScrapeException;
}
