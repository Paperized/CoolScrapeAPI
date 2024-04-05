package com.paperized.shopapi.controller;

import com.paperized.generated.shopapi.api.TcgStoreApi;
import com.paperized.generated.shopapi.model.FindSummaryProductsRequest;
import com.paperized.generated.shopapi.model.TcgProductDto;
import com.paperized.generated.shopapi.model.TcgProductsTracked;
import com.paperized.shopapi.service.TcgStoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TcgStoreController implements TcgStoreApi {
    private final TcgStoreService tcgStoreService;

    public TcgStoreController(TcgStoreService tcgStoreService) {
        this.tcgStoreService = tcgStoreService;
    }

    @Override
    public ResponseEntity<TcgProductsTracked> findSummaryProductsTrack(String webhookUrl, Boolean onlyIfDifferent, Boolean sendOnlyDifferences, FindSummaryProductsRequest findSummaryProductsRequest) throws Exception {
        TcgProductsTracked trackableResult = tcgStoreService.findSummaryProductsTracked(findSummaryProductsRequest.getPageNum());

        // Ignore onlyIfDifferent and sendOnlyDifferences for this return, will be checked in the schedule
        if(findSummaryProductsRequest.getQuery() != null) {
            findSummaryProductsRequest.getQuery().filterQueriables(trackableResult.getItems());
        }

        return ResponseEntity.ok(trackableResult);
    }

    @Override
    public ResponseEntity<List<TcgProductDto>> findSummaryProducts(FindSummaryProductsRequest findSummaryProductsRequest) throws Exception {
        return ResponseEntity.ok(tcgStoreService.findSummaryProducts(findSummaryProductsRequest.getPageNum()));
    }
}
