package com.paperized.shopapi.controller;

import com.paperized.generated.shopapi.api.SteamApi;
import com.paperized.generated.shopapi.model.SteamProfileDto;
import com.paperized.generated.shopapi.model.SteamProfileTracked;
import com.paperized.shopapi.model.webhookfilter.DOnChangeMode;
import com.paperized.shopapi.model.webhookfilter.DOnChangeSaveMode;
import com.paperized.shopapi.model.webhookfilter.DOnChanges;
import com.paperized.shopapi.model.webhookfilter.DQueryRequestWebhook;
import com.paperized.shopapi.service.SteamService;
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
    public ResponseEntity<SteamProfileTracked> findSteamProfileTrack(String url, String webhookUrl, DOnChangeMode checkMode, DOnChangeSaveMode saveMode, List<String> propertiesToCheck) throws Exception {
        DQueryRequestWebhook queryRequestWebhook = new DQueryRequestWebhook();
        queryRequestWebhook.setPreviousDataChecks(DOnChanges.builder()
                .mode(checkMode)
                .saveMode(saveMode)
                .propertiesToCheck(propertiesToCheck)
                .build());

        return ResponseEntity.ok(steamService.findSteamProfileTracked(url, webhookUrl, queryRequestWebhook));
    }
}
