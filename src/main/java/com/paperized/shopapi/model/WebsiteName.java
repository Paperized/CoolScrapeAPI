package com.paperized.shopapi.model;

import lombok.Getter;

@Getter
public enum WebsiteName {
    TrovaPrezzi("trovaPrezzi"),
    Amazon("amazon");

    private final String nameInConfig;

    WebsiteName(String nameInConfig) {
        this.nameInConfig = nameInConfig;
    }
}
