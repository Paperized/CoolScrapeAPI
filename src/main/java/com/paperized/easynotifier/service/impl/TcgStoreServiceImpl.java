package com.paperized.easynotifier.service.impl;

import com.paperized.generated.easynotifier.model.TcgProductDto;
import com.paperized.generated.easynotifier.model.TcgProductsTracked;
import com.paperized.generated.easynotifier.model.TrackerInfoDto;
import com.paperized.easynotifier.dquery.DQueryRequest;
import com.paperized.easynotifier.model.webhookfilter.DQueryRequestWebhook;
import com.paperized.easynotifier.exceptions.TrackingAlreadyScheduledException;
import com.paperized.easynotifier.exceptions.UnsuccessfulScrapeException;
import com.paperized.easynotifier.model.TrackerAction;
import com.paperized.easynotifier.model.WebsiteName;
import com.paperized.easynotifier.scraper.TcgStoreScraper;
import com.paperized.easynotifier.service.ProductTrackerScheduler;
import com.paperized.easynotifier.service.TcgStoreService;
import com.paperized.easynotifier.service.ProductTrackerService;
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
