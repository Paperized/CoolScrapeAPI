package com.paperized.shopapi.service;

import com.paperized.generated.shopapi.model.TcgProductDto;
import com.paperized.generated.shopapi.model.TcgProductsTracked;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import org.jsoup.HttpStatusException;

import java.util.List;

public interface TcgStoreService {
    TcgProductsTracked findSummaryProductsTracked(Integer page) throws HttpStatusException, UnsuccessfulScrapeException;
    List<TcgProductDto> findSummaryProducts(Integer page) throws HttpStatusException, UnsuccessfulScrapeException;
}
