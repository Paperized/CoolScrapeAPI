package com.paperized.shopapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class WebsiteSetting {
    private List<String> availableHeaders;
    private Map<String, String> extraHeaders;
    private Map<String, String> customProperties;

    public String getProp(String prop) {
        return customProperties.get(prop);
    }

    @Getter
    public enum WebsiteName {
        TrovaPrezzi("trovaPrezzi");

        private final String nameInConfig;

        WebsiteName(String nameInConfig) {
            this.nameInConfig = nameInConfig;
        }
    }
}
