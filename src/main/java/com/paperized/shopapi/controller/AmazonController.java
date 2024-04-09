package com.paperized.shopapi.controller;

import com.paperized.generated.shopapi.api.AmazonApi;
import com.paperized.generated.shopapi.model.AmazonProductDto;
import com.paperized.generated.shopapi.model.AmazonProductTracked;
import com.paperized.shopapi.dquery.DQueryRequest;
import com.paperized.shopapi.model.webhookfilter.DOnChangeMode;
import com.paperized.shopapi.model.webhookfilter.DOnChangeSaveMode;
import com.paperized.shopapi.model.webhookfilter.DOnChanges;
import com.paperized.shopapi.model.webhookfilter.DQueryRequestWebhook;
import com.paperized.shopapi.service.AmazonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AmazonController implements AmazonApi {
    private final AmazonService amazonService;

    public AmazonController(AmazonService amazonService) {
        this.amazonService = amazonService;
    }

    @Override
    public ResponseEntity<AmazonProductDto> findProductDetails(String url) throws Exception {
        return ResponseEntity.ok(amazonService.findProductDetails(url));
    }

    @Override
    public ResponseEntity<AmazonProductTracked> findProductDetailsTrack(String url, String webhookUrl, DOnChangeMode checkMode, DOnChangeSaveMode saveMode, List<String> propertiesToCheck, DQueryRequest body) throws Exception {
        DQueryRequestWebhook queryRequestWebhook = new DQueryRequestWebhook(body);
        queryRequestWebhook.setPreviousDataChecks(DOnChanges.builder()
                .mode(checkMode)
                .saveMode(saveMode)
                .propertiesToCheck(propertiesToCheck)
                .build());

        return ResponseEntity.ok(amazonService.findProductDetailsTracked(url, webhookUrl, queryRequestWebhook));
    }
}
