package com.paperized.easynotifier.controller;

import com.paperized.generated.easynotifier.api.AmazonApi;
import com.paperized.generated.easynotifier.model.AmazonProductDto;
import com.paperized.generated.easynotifier.model.AmazonProductTracked;
import com.paperized.easynotifier.dquery.DQueryRequest;
import com.paperized.easynotifier.model.webhookfilter.DOnChangeMode;
import com.paperized.easynotifier.model.webhookfilter.DOnChangeSaveMode;
import com.paperized.easynotifier.model.webhookfilter.DOnChanges;
import com.paperized.easynotifier.model.webhookfilter.DQueryRequestWebhook;
import com.paperized.easynotifier.service.AmazonService;
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
