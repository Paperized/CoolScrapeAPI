package com.paperized.easynotifier.controller;

import com.paperized.easynotifier.dto.TrackListeningDto;
import com.paperized.easynotifier.utils.AppUtils;
import com.paperized.generated.easynotifier.api.SteamApi;
import com.paperized.generated.easynotifier.model.SteamProfileDto;
import com.paperized.generated.easynotifier.model.SteamProfileTracked;
import com.paperized.easynotifier.model.webhookfilter.DOnChangeMode;
import com.paperized.easynotifier.model.webhookfilter.DOnChangeSaveMode;
import com.paperized.easynotifier.model.webhookfilter.DOnChanges;
import com.paperized.easynotifier.model.webhookfilter.DQueryRequestScheduled;
import com.paperized.easynotifier.service.SteamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SteamController implements SteamApi {
    private final SteamService steamService;

    public SteamController(SteamService steamService) {
        this.steamService = steamService;
    }

    @Override
    public ResponseEntity<SteamProfileDto> findSteamProfile(String url) throws Exception {
        return ResponseEntity.ok(steamService.findSteamProfile(url));
    }

    @Override
    public ResponseEntity<SteamProfileTracked> findSteamProfileTrack(String url, String webhookUrl, Boolean wsEnabled, DOnChangeMode checkMode, DOnChangeSaveMode saveMode, List<String> propertiesToCheck) throws Exception {
        AppUtils.checkWsOrWebhookMandatory(webhookUrl, wsEnabled);

        DQueryRequestScheduled queryRequestWebhook = new DQueryRequestScheduled();
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

        return ResponseEntity.ok(steamService.findSteamProfileTracked(url, trackListeningDto));
    }
}
