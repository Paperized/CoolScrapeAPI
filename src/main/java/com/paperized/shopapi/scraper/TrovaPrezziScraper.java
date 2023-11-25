package com.paperized.shopapi.scraper;

import com.paperized.generated.shopapi.model.TrovaprezziProduct;

import java.util.List;

public interface TrovaPrezziScraper {
    List<TrovaprezziProduct> searchProduct(String name);
}
