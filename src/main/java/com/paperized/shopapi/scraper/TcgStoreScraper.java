package com.paperized.shopapi.scraper;

import com.paperized.generated.shopapi.model.TcgProductDto;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import org.jsoup.HttpStatusException;

import java.util.List;

public interface TcgStoreScraper {
    List<TcgProductDto> findSummaryProducts(int page) throws HttpStatusException, UnsuccessfulScrapeException;

    String getSummaryProductsUrl(int page);

    List<TcgProductDto> findSummaryAllProducts() throws HttpStatusException, UnsuccessfulScrapeException;
}
