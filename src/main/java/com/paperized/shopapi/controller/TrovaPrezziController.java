package com.paperized.shopapi.controller;

import com.paperized.generated.shopapi.api.TrovaprezziApi;
import com.paperized.generated.shopapi.model.TrovaprezziProduct;
import com.paperized.shopapi.service.TrovaPrezziService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TrovaPrezziController implements TrovaprezziApi {
    private final TrovaPrezziService trovaPrezziService;

    public TrovaPrezziController(TrovaPrezziService trovaPrezziService) {
        this.trovaPrezziService = trovaPrezziService;
    }

    @Override
    public ResponseEntity<List<TrovaprezziProduct>> searchProducts(String name) {
        return ResponseEntity.ok(trovaPrezziService.searchProducts(name));
    }
}
