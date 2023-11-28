package com.paperized.shopapi.service;

import com.paperized.generated.shopapi.model.AmazonProduct;
import org.jsoup.HttpStatusException;

public interface AmazonService {
    AmazonProduct findProductDetails(String url) throws HttpStatusException;
}
