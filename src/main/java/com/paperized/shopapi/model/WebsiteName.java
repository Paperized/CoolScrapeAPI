package com.paperized.shopapi.model;

import lombok.Getter;

@Getter
public enum WebsiteName {
    Amazon("amazon"),
    TcgStore("tcg-store");

    private final String nameInConfig;

    WebsiteName(String nameInConfig) {
        this.nameInConfig = nameInConfig;
    }
}
