package com.paperized.shopapi.model;

import lombok.Getter;

@Getter
public enum TrackingAction {
    AMAZON_PRODUCT_DETAILS(false),
    TCGSTORE_SUMMARY_PRODUCTS(true),
    TCGSTORE_ALL_SUMMARY_PRODUCTS(true);

    private final boolean returnsList;

    TrackingAction(boolean returnsList) {
        this.returnsList = returnsList;
    }
}
