package com.paperized.shopapi.model;

import com.paperized.generated.shopapi.model.AmazonProductDto;
import com.paperized.generated.shopapi.model.SteamProfileDto;
import com.paperized.generated.shopapi.model.TcgProductDto;
import com.paperized.shopapi.dquery.DQueriable;
import lombok.Getter;

@Getter
public enum TrackingAction {
    AMAZON_PRODUCT_DETAILS(false, AmazonProductDto.class),
    TCGSTORE_SUMMARY_PRODUCTS(true, TcgProductDto.class),
    TCGSTORE_ALL_SUMMARY_PRODUCTS(true, TcgProductDto.class),
    STEAM_FIND_PROFILE(false, SteamProfileDto.class);

    private final boolean returnsList;
    private final Class<? extends DQueriable> clazz;

    TrackingAction(boolean returnsList, Class<? extends DQueriable> clazz) {
        this.returnsList = returnsList;
        this.clazz = clazz;
    }
}
