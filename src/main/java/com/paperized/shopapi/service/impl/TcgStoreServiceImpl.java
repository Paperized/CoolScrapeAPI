package com.paperized.shopapi.service.impl;

import com.paperized.generated.shopapi.model.TcgProductDto;
import com.paperized.generated.shopapi.model.TcgProductsTracked;
import com.paperized.generated.shopapi.model.TrackerInfoDto;
import com.paperized.shopapi.dquery.DQueryRequest;
import com.paperized.shopapi.model.webhookfilter.DQueryRequestWebhook;
import com.paperized.shopapi.exceptions.TrackingAlreadyScheduledException;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import com.paperized.shopapi.model.TrackerAction;
import com.paperized.shopapi.model.WebsiteName;
import com.paperized.shopapi.scraper.TcgStoreScraper;
import com.paperized.shopapi.service.ProductTrackerScheduler;
import com.paperized.shopapi.service.TcgStoreService;
import com.paperized.shopapi.service.ProductTrackerService;
import org.jsoup.HttpStatusException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TcgStoreServiceImpl implements TcgStoreService {
    private final TcgStoreScraper tcgStoreScraper;
    private final ProductTrackerService productTrackerService;
    private final ProductTrackerScheduler productTrackerScheduler;

    public TcgStoreServiceImpl(TcgStoreScraper tcgStoreScraper, ProductTrackerService productTrackerService, ProductTrackerScheduler productTrackerScheduler) {
        this.tcgStoreScraper = tcgStoreScraper;
        this.productTrackerService = productTrackerService;
        this.productTrackerScheduler = productTrackerScheduler;
    }

    @Override
    public TcgProductsTracked findSummaryProductsTracked(Integer page, String webhookUrl, DQueryRequestWebhook queryRequestWebhook) throws HttpStatusException, UnsuccessfulScrapeException {
        List<TcgProductDto> tcgProductsDto = findSummaryProducts(page, queryRequestWebhook);
        TrackerAction trackerAction = page != null ? TrackerAction.TCGSTORE_SUMMARY_PRODUCTS : TrackerAction.TCGSTORE_ALL_SUMMARY_PRODUCTS;
        String trackerId = productTrackerService.trackNewProduct(tcgStoreScraper.getSummaryProductsUrl(page), WebsiteName.TcgStore, trackerAction, webhookUrl, queryRequestWebhook);

        try {
            productTrackerScheduler.scheduleTracker(trackerId, true);
        } catch (TrackingAlreadyScheduledException e) {
            throw new RuntimeException("Inconsistency, Created Tracker with id " + trackerId + " but it's already defined in the in-memory scheduler");
        }

        return new TcgProductsTracked()
                .items(tcgProductsDto)
                .track(new TrackerInfoDto().trackerId(trackerId));
    }

    @Override
    public List<TcgProductDto> findSummaryProducts(Integer page, DQueryRequest request) throws HttpStatusException, UnsuccessfulScrapeException {
        List<TcgProductDto> dtos = page != null ? tcgStoreScraper.findSummaryProducts(page) : tcgStoreScraper.findSummaryAllProducts();
        request.filterQueriables(dtos);

        return dtos;

    }
}
