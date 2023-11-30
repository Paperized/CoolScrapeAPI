package com.paperized.shopapi.config;

import com.paperized.shopapi.dto.WebsiteSetting;
import com.paperized.shopapi.model.WebsiteName;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "scraper-settings")
@Getter
@Setter
public class ScraperSettings {
    private Map<WebsiteName, WebsiteSetting> websitesSettings;

    public WebsiteSetting fromName(@NotNull WebsiteName websiteName) {
        return websitesSettings.get(websiteName);
    }
}
