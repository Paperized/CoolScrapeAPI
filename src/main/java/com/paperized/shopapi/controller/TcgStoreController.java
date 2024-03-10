package com.paperized.shopapi.controller;

import com.paperized.generated.shopapi.api.TcgStoreApi;
import com.paperized.generated.shopapi.model.FindSummaryProductsRequest;
import com.paperized.generated.shopapi.model.TcgProductsTracked;
import com.paperized.shopapi.service.TcgStoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TcgStoreController implements TcgStoreApi {
    private final TcgStoreService tcgStoreService;

    public TcgStoreController(TcgStoreService tcgStoreService) {
        this.tcgStoreService = tcgStoreService;
    }

    @Override
    public ResponseEntity<TcgProductsTracked> findSummaryProducts(FindSummaryProductsRequest body) throws Exception {
        TcgProductsTracked trackableResult;

        if(body.getPageNum() != null) {
            trackableResult = tcgStoreService.findSummaryProducts(body.getPageNum());
        } else {
            trackableResult = tcgStoreService.findSummaryAllProducts();
        }

        body.getQuery().filterQueriables(trackableResult.getItems());
        return ResponseEntity.ok(trackableResult);
    }
}
