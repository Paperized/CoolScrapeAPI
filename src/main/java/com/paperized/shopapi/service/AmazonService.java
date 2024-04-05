package com.paperized.shopapi.service;

import com.paperized.generated.shopapi.model.AmazonProductDto;
import com.paperized.generated.shopapi.model.AmazonProductTracked;
import com.paperized.shopapi.dto.DQueryRequestWebhook;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import org.jsoup.HttpStatusException;

public interface AmazonService {
    AmazonProductTracked findProductDetailsTracked(String url, String webhookUrl, DQueryRequestWebhook queryRequestWebhook) throws HttpStatusException, UnsuccessfulScrapeException;
    AmazonProductDto findProductDetails(String url) throws HttpStatusException, UnsuccessfulScrapeException;
}
