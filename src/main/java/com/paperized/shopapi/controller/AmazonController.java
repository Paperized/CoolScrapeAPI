package com.paperized.shopapi.controller;

import com.paperized.generated.shopapi.api.AmazonApi;
import com.paperized.generated.shopapi.model.AmazonProductDto;
import com.paperized.generated.shopapi.model.AmazonProductTracked;
import com.paperized.shopapi.dquery.DQueryRequest;
import com.paperized.shopapi.dto.DQueryRequestWebhook;
import com.paperized.shopapi.service.AmazonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<AmazonProductTracked> findProductDetailsTrack(String url, String webhookUrl, Boolean onlyIfDifferent, DQueryRequest body) throws Exception {
        DQueryRequestWebhook queryRequestWebhook = new DQueryRequestWebhook(body);
        queryRequestWebhook.setOnlyIfDifferent(onlyIfDifferent);

        return ResponseEntity.ok(amazonService.findProductDetailsTracked(url, webhookUrl, queryRequestWebhook));
    }
}
