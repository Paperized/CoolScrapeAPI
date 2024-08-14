package com.paperized.easynotifier.controller;

import com.paperized.easynotifier.dto.TrackListeningDto;
import com.paperized.easynotifier.utils.AppUtils;
import com.paperized.generated.easynotifier.api.AmazonApi;
import com.paperized.generated.easynotifier.model.AmazonProductDto;
import com.paperized.generated.easynotifier.model.AmazonProductTracked;
import com.paperized.easynotifier.dquery.DQueryRequest;
import com.paperized.easynotifier.model.webhookfilter.DOnChangeMode;
import com.paperized.easynotifier.model.webhookfilter.DOnChangeSaveMode;
import com.paperized.easynotifier.model.webhookfilter.DOnChanges;
import com.paperized.easynotifier.model.webhookfilter.DQueryRequestScheduled;
import com.paperized.easynotifier.service.AmazonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.midi.Track;
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
    public ResponseEntity<AmazonProductTracked> findProductDetailsTrack(String url, String webhookUrl, Boolean wsEnabled, DOnChangeMode checkMode, DOnChangeSaveMode saveMode, List<String> propertiesToCheck, DQueryRequest body) throws Exception {
        AppUtils.checkWsOrWebhookMandatory(webhookUrl, wsEnabled);

        DQueryRequestScheduled queryRequestWebhook = new DQueryRequestScheduled(body);
        queryRequestWebhook.setPreviousDataChecks(DOnChanges.builder()
                .mode(checkMode)
                .saveMode(saveMode)
                .propertiesToCheck(propertiesToCheck)
                .build());

        TrackListeningDto trackListeningDto = TrackListeningDto.builder()
                .dQueryRequestScheduled(queryRequestWebhook)
                .webhookUrl(webhookUrl)
                .wsEnabled(Boolean.TRUE.equals(wsEnabled))
                .build();

        return ResponseEntity.ok(amazonService.findProductDetailsTracked(url, trackListeningDto));
    }
}
