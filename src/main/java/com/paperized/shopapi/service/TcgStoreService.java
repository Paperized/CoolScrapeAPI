package com.paperized.shopapi.service;

import com.paperized.generated.shopapi.model.TcgProductDto;
import com.paperized.generated.shopapi.model.TcgProductsTracked;
import com.paperized.shopapi.dquery.DQueryRequest;
import com.paperized.shopapi.dto.DQueryRequestWebhook;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import org.jsoup.HttpStatusException;

import java.util.List;

public interface TcgStoreService {
    TcgProductsTracked findSummaryProductsTracked(Integer page, String webhookUrl, DQueryRequestWebhook queryRequestWebhook) throws HttpStatusException, UnsuccessfulScrapeException;
    List<TcgProductDto> findSummaryProducts(Integer page, DQueryRequest queryRequest) throws HttpStatusException, UnsuccessfulScrapeException;
}
