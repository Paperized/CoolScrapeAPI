package com.paperized.shopapi.service;

import com.paperized.generated.shopapi.model.TrovaprezziProduct;

import java.util.List;

public interface TrovaPrezziService {
    List<TrovaprezziProduct> searchProducts(String name);
}
