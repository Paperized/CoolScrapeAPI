package com.paperized.shopapi.controller;

import com.paperized.generated.shopapi.api.TcgStoreApi;
import com.paperized.generated.shopapi.model.FindSummaryProductsRequest;
import com.paperized.generated.shopapi.model.TcgProductDto;
import com.paperized.generated.shopapi.model.TcgProductsTracked;
import com.paperized.shopapi.model.webhookfilter.DOnChangeMode;
import com.paperized.shopapi.model.webhookfilter.DOnChangeSaveMode;
import com.paperized.shopapi.model.webhookfilter.DOnChanges;
import com.paperized.shopapi.model.webhookfilter.DQueryRequestWebhook;
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
    public ResponseEntity<TcgProductsTracked> findSummaryProductsTrack(String webhookUrl, DOnChangeMode checkMode, DOnChangeSaveMode saveMode, List<String> propertiesToCheck, FindSummaryProductsRequest findSummaryProductsRequest) throws Exception {
        DQueryRequestWebhook queryRequestWebhook = new DQueryRequestWebhook(findSummaryProductsRequest.getQuery());
        queryRequestWebhook.setPreviousDataChecks(DOnChanges.builder()
                .mode(checkMode)
                .saveMode(saveMode)
                .propertiesToCheck(propertiesToCheck)
                .build());

        return ResponseEntity.ok(tcgStoreService.findSummaryProductsTracked(findSummaryProductsRequest.getPageNum(), webhookUrl, queryRequestWebhook));
    }

    @Override
    public ResponseEntity<List<TcgProductDto>> findSummaryProducts(FindSummaryProductsRequest findSummaryProductsRequest) throws Exception {
        return ResponseEntity.ok(tcgStoreService.findSummaryProducts(findSummaryProductsRequest.getPageNum(), findSummaryProductsRequest.getQuery()));
    }
}
