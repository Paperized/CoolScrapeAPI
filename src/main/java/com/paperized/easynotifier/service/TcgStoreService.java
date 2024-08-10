package com.paperized.easynotifier.service;

import com.paperized.generated.easynotifier.model.TcgProductDto;
import com.paperized.generated.easynotifier.model.TcgProductsTracked;
import com.paperized.easynotifier.dquery.DQueryRequest;
import com.paperized.easynotifier.model.webhookfilter.DQueryRequestWebhook;
import com.paperized.easynotifier.exceptions.UnsuccessfulScrapeException;
import org.jsoup.HttpStatusException;

import java.util.List;

public interface TcgStoreService {
    TcgProductsTracked findSummaryProductsTracked(Integer page, String webhookUrl, DQueryRequestWebhook queryRequestWebhook) throws HttpStatusException, UnsuccessfulScrapeException;
    List<TcgProductDto> findSummaryProducts(Integer page, DQueryRequest queryRequest) throws HttpStatusException, UnsuccessfulScrapeException;
}
