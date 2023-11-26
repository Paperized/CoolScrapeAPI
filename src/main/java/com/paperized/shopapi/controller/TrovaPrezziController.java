package com.paperized.shopapi.controller;

import com.paperized.generated.shopapi.api.TrovaprezziApi;
import com.paperized.generated.shopapi.model.TrovaPrezziProduct;
import com.paperized.generated.shopapi.model.TrovaPrezziSort;
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
    public ResponseEntity<List<TrovaPrezziProduct>> searchProducts(String category, String search, Integer page, List<String> filters, Integer minPrice, Integer maxPrice, Integer rating, TrovaPrezziSort sort) {
        return ResponseEntity.ok(trovaPrezziService.searchProducts(search, category, page, filters, minPrice, maxPrice, rating, sort));
    }
}
