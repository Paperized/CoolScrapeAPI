package com.paperized.shopapi.controller;

import com.paperized.generated.shopapi.api.TrovaprezziApi;
import com.paperized.generated.shopapi.model.TrovaPrezziProduct;
import com.paperized.generated.shopapi.model.TrovaPrezziSort;
import com.paperized.shopapi.service.TrovaPrezziService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TrovaPrezziController implements TrovaprezziApi {
    private final TrovaPrezziService trovaPrezziService;

    public TrovaPrezziController(TrovaPrezziService trovaPrezziService) {
        this.trovaPrezziService = trovaPrezziService;
    }

    @Override
    public ResponseEntity<List<TrovaPrezziProduct>> searchProducts(Integer category, String search, Integer page, List<String> filters, Integer minPrice, Integer maxPrice, Integer rating, TrovaPrezziSort sort) throws Exception {
        return ResponseEntity.ok(trovaPrezziService.searchProducts(search, category, page, filters, minPrice, maxPrice, rating, sort));
    }

    @Override
    public ResponseEntity<Map<String, List<String>>> listCategories() throws Exception {
        return TrovaprezziApi.super.listCategories();
    }
}
