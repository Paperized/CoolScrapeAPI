package com.paperized.shopapi.scraper;

import com.paperized.generated.shopapi.model.AmazonProductDto;
import org.jsoup.HttpStatusException;

public interface AmazonScraper {
    AmazonProductDto findProductDetails(String url) throws HttpStatusException;
}
