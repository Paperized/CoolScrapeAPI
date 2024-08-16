package com.paperized.easynotifier.model;

import lombok.Getter;

@Getter
public enum WebsiteName {
    Linkedin("linkedin"),
    Amazon("amazon"),
    TcgStore("tcg-store"),
    Steam("steam");

    private final String nameInConfig;

    WebsiteName(String nameInConfig) {
        this.nameInConfig = nameInConfig;
    }
}
