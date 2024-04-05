package com.paperized.shopapi.controller;

import com.paperized.generated.shopapi.api.SteamApi;
import com.paperized.generated.shopapi.model.SteamProfileDto;
import com.paperized.generated.shopapi.model.SteamProfileTracked;
import com.paperized.shopapi.service.SteamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<SteamProfileTracked> findSteamProfileTrack(String url, String webhookUrl, Boolean sendOnlyDifferences) throws Exception {
        return ResponseEntity.ok(steamService.findSteamProfileTracked(url));
    }
}
