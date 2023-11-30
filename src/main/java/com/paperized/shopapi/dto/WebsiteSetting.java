package com.paperized.shopapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class WebsiteSetting {
    private String baseUrl;
    private List<String> availableHeaders;
    private Map<String, String> extraHeaders;
    private Map<String, String> customProperties;

    public String getProp(String prop) {
        return customProperties.get(prop);
    }
}
