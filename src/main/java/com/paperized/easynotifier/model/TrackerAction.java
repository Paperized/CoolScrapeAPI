package com.paperized.easynotifier.model;

import com.paperized.generated.easynotifier.model.AmazonProductDto;
import com.paperized.generated.easynotifier.model.LinkedinCandidateDto;
import com.paperized.generated.easynotifier.model.SteamProfileDto;
import com.paperized.generated.easynotifier.model.TcgProductDto;
import com.paperized.easynotifier.dquery.DQueriable;
import lombok.Getter;

@Getter
public enum TrackerAction {
    LINKEDIN_FIND_CANDIDATES(true, LinkedinCandidateDto.class),
    AMAZON_PRODUCT_DETAILS(false, AmazonProductDto.class),
    TCGSTORE_SUMMARY_PRODUCTS(true, TcgProductDto.class),
    TCGSTORE_ALL_SUMMARY_PRODUCTS(true, TcgProductDto.class),
    STEAM_FIND_PROFILE(false, SteamProfileDto.class);

    private final boolean returnsList;
    private final Class<? extends DQueriable> clazz;

    TrackerAction(boolean returnsList, Class<? extends DQueriable> clazz) {
        this.returnsList = returnsList;
        this.clazz = clazz;
    }
}
