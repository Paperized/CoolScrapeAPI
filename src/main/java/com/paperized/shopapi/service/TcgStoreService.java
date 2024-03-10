package com.paperized.shopapi.service;

import com.paperized.generated.shopapi.model.TcgProductsTracked;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import org.jsoup.HttpStatusException;

public interface TcgStoreService {
    TcgProductsTracked findSummaryProducts(int page) throws HttpStatusException, UnsuccessfulScrapeException;
    TcgProductsTracked findSummaryAllProducts() throws HttpStatusException, UnsuccessfulScrapeException;
}
