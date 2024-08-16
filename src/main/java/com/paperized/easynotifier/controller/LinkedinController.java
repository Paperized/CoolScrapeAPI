package com.paperized.easynotifier.controller;

import com.paperized.easynotifier.dto.TrackListeningDto;
import com.paperized.easynotifier.model.webhookfilter.DOnChangeMode;
import com.paperized.easynotifier.model.webhookfilter.DOnChangeSaveMode;
import com.paperized.easynotifier.model.webhookfilter.DOnChanges;
import com.paperized.easynotifier.model.webhookfilter.DQueryRequestScheduled;
import com.paperized.easynotifier.service.LinkedinService;
import com.paperized.easynotifier.utils.AppUtils;
import com.paperized.generated.easynotifier.api.LinkedinApi;
import com.paperized.generated.easynotifier.model.FindCandidatesRequest;
import com.paperized.generated.easynotifier.model.LinkedinCandidatesTracked;
import com.paperized.generated.easynotifier.model.TcgProductsTracked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LinkedinController implements LinkedinApi {
    private final LinkedinService linkedinService;

    public LinkedinController(LinkedinService linkedinService) {
        this.linkedinService = linkedinService;
    }

    @Override
    public ResponseEntity<LinkedinCandidatesTracked> findCandidates(String webhookUrl, Boolean wsEnabled, DOnChangeMode checkMode, DOnChangeSaveMode saveMode, List<String> propertiesToCheck, FindCandidatesRequest findCandidatesRequest) throws Exception {
        LinkedinCandidatesTracked result;
        boolean isTracked = AppUtils.hasWsOrWebhook(webhookUrl, wsEnabled);
        if(isTracked) {
            DQueryRequestScheduled queryRequestWebhook = new DQueryRequestScheduled(findCandidatesRequest.getQuery());
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

            result = linkedinService.findCandidatesTracked(findCandidatesRequest, trackListeningDto);
        } else {
            result = new LinkedinCandidatesTracked()
                    .items(linkedinService.findCandidates(findCandidatesRequest))
                    .track(null);
        }

        return ResponseEntity.ok(result);
    }
}
