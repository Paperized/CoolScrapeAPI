package com.paperized.shopapi.service.impl;

import com.paperized.generated.shopapi.model.TrovaPrezziProduct;
import com.paperized.generated.shopapi.model.TrovaPrezziSort;
import com.paperized.shopapi.scraper.TrovaPrezziScraper;
import com.paperized.shopapi.service.TrovaPrezziService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrovaPrezziServiceImpl implements TrovaPrezziService {
    private final TrovaPrezziScraper trovaPrezziScraper;

    public TrovaPrezziServiceImpl(TrovaPrezziScraper trovaPrezziScraper) {
        this.trovaPrezziScraper = trovaPrezziScraper;
    }

    @Override
    public List<TrovaPrezziProduct> searchProducts(String search, String category, Integer page, List<String> filters, Integer minPrice, Integer maxPrice, Integer rating, TrovaPrezziSort sort) {
        return trovaPrezziScraper.searchProduct(search, category, page, filters, minPrice, maxPrice, rating, sort);
    }
}
