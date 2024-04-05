package com.paperized.shopapi.service.impl;

import com.paperized.generated.shopapi.model.ProductTrackingDto;
import com.paperized.generated.shopapi.model.TcgProductDto;
import com.paperized.generated.shopapi.model.TcgProductsTracked;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import com.paperized.shopapi.model.TrackingAction;
import com.paperized.shopapi.model.WebsiteName;
import com.paperized.shopapi.scraper.TcgStoreScraper;
import com.paperized.shopapi.service.TcgStoreService;
import com.paperized.shopapi.service.TrackingService;
import org.jsoup.HttpStatusException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TcgStoreServiceImpl implements TcgStoreService {
    private final TcgStoreScraper tcgStoreScraper;
    private final TrackingService trackingService;

    public TcgStoreServiceImpl(TcgStoreScraper tcgStoreScraper, TrackingService trackingService) {
        this.tcgStoreScraper = tcgStoreScraper;
        this.trackingService = trackingService;
    }

    @Override
    public TcgProductsTracked findSummaryProductsTracked(Integer page) throws HttpStatusException, UnsuccessfulScrapeException {
        List<TcgProductDto> tcgProductsDto = findSummaryProducts(page);
        TrackingAction trackingAction = page != null ? TrackingAction.TCGSTORE_SUMMARY_PRODUCTS : TrackingAction.TCGSTORE_ALL_SUMMARY_PRODUCTS;
        ProductTrackingDto tracking = trackingService.generateNewTracking(tcgStoreScraper.getSummaryProductsUrl(page), WebsiteName.TcgStore, trackingAction);

        return new TcgProductsTracked()
                .items(tcgProductsDto)
                .track(tracking);
    }

    @Override
    public List<TcgProductDto> findSummaryProducts(Integer page) throws HttpStatusException, UnsuccessfulScrapeException {
        return page != null ? tcgStoreScraper.findSummaryProducts(page) : tcgStoreScraper.findSummaryAllProducts();
    }
}
