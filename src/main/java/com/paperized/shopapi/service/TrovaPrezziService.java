package com.paperized.shopapi.service;

import com.paperized.generated.shopapi.model.TrovaPrezziProduct;
import com.paperized.generated.shopapi.model.TrovaPrezziSort;
import org.jsoup.HttpStatusException;

import java.util.List;

public interface TrovaPrezziService {
    List<TrovaPrezziProduct> searchProducts(String search, Integer category, Integer page, List<String> filters, Integer minPrice, Integer maxPrice, Integer rating, TrovaPrezziSort sort) throws HttpStatusException;
}
