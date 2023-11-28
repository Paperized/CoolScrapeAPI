package com.paperized.shopapi.scraper;

import com.paperized.generated.shopapi.model.AmazonProduct;
import org.jsoup.HttpStatusException;

public interface AmazonScraper {
    AmazonProduct findProductDetails(String url) throws HttpStatusException;
}
