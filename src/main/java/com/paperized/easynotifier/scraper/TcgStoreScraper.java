package com.paperized.easynotifier.scraper;

import com.paperized.generated.easynotifier.model.TcgProductDto;
import com.paperized.easynotifier.exceptions.UnsuccessfulScrapeException;
import org.jsoup.HttpStatusException;

import java.util.List;

public interface TcgStoreScraper {
    List<TcgProductDto> findSummaryProducts(int page) throws HttpStatusException, UnsuccessfulScrapeException;
    List<TcgProductDto> findSummaryAllProducts() throws HttpStatusException, UnsuccessfulScrapeException;
    String getSummaryProductsUrl(Integer page);
}
