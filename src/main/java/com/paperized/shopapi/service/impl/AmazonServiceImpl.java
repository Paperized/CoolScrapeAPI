package com.paperized.shopapi.service.impl;

import com.paperized.generated.shopapi.model.AmazonProduct;
import com.paperized.shopapi.scraper.AmazonScraper;
import com.paperized.shopapi.service.AmazonService;
import org.jsoup.HttpStatusException;
import org.springframework.stereotype.Service;

@Service
public class AmazonServiceImpl implements AmazonService {
    private final AmazonScraper amazonScraper;

    public AmazonServiceImpl(AmazonScraper amazonScraper) {
        this.amazonScraper = amazonScraper;
    }

    @Override
    public AmazonProduct findProductDetails(String url) throws HttpStatusException {
        return amazonScraper.findProductDetails(url);
    }
}
