package com.paperized.shopapi.scraper;

import com.paperized.generated.shopapi.model.TrovaPrezziProduct;
import com.paperized.generated.shopapi.model.TrovaPrezziSort;
import org.jsoup.HttpStatusException;

import java.util.List;
import java.util.Map;

public interface TrovaPrezziScraper {
    List<TrovaPrezziProduct> searchProduct(String search, Integer category, Integer page, List<String> filters, Integer minPrice, Integer maxPrice, Integer rating, TrovaPrezziSort sort) throws HttpStatusException;
    Map<String, List<String>> listCategories();
}
