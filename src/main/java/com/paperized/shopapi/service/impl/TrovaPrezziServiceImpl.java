package com.paperized.shopapi.service.impl;

import com.paperized.generated.shopapi.model.TrovaprezziProduct;
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
    public List<TrovaprezziProduct> searchProducts(String name) {
        return trovaPrezziScraper.searchProduct(name);
    }
}
