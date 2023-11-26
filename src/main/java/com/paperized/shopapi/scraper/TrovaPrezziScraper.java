package com.paperized.shopapi.scraper;

import com.paperized.generated.shopapi.model.TrovaPrezziProduct;
import com.paperized.generated.shopapi.model.TrovaPrezziSort;

import java.util.List;

public interface TrovaPrezziScraper {
    List<TrovaPrezziProduct> searchProduct(String search, String category, Integer page, List<String> filters, Integer minPrice, Integer maxPrice, Integer rating, TrovaPrezziSort sort);
}
