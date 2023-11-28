package com.paperized.shopapi.scraper.impl;

import com.paperized.generated.shopapi.model.AmazonProduct;
import com.paperized.shopapi.config.ScraperSettings;
import com.paperized.shopapi.dto.WebsiteSetting;
import com.paperized.shopapi.scraper.AmazonScraper;
import com.paperized.shopapi.scraper.ScraperHttpService;
import com.paperized.shopapi.utils.ScraperUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class AmazonScraperImpl implements AmazonScraper {
    Logger logger = LoggerFactory.getLogger(AmazonScraperImpl.class);
    private final ScraperHttpService scraperHttpService;
    private final WebsiteSetting websiteSetting;

    public AmazonScraperImpl(ScraperHttpService scraperHttpService, ScraperSettings scraperSettings) {
        this.scraperHttpService = scraperHttpService;
        websiteSetting = scraperSettings.fromName(WebsiteSetting.WebsiteName.Amazon);
    }

    @Override
    public AmazonProduct findProductDetails(String url) throws HttpStatusException {
        AmazonProduct amazonProduct = new AmazonProduct();
        Document page = scraperHttpService.getPage(url, websiteSetting);
        logger.info("Loaded amazon page: {}", page.location());

        amazonProduct.setName(ScraperUtils.getText(page.getElementById("productTitle")));

        var pricePanel = page.selectXpath("//*[starts-with(@id, 'corePrice_desktop')]").first();
        if(pricePanel != null) {
            var lastPrice = ScraperUtils.getText(pricePanel.selectXpath(".//*[@data-a-strike='true']/*[1]").first());
            var currentPrice = ScraperUtils.getText(pricePanel.selectXpath(".//*[@id='taxInclusiveMessage']/../*[@data-a-color='price']/*[1]").first());
            var savingPrice = ScraperUtils.getText(pricePanel.selectXpath("//*[contains(@id, 'bundleLTBSSavings')]/..//*[@data-a-color='price']/*[1]").first());
            amazonProduct.lastPrice(lastPrice).currentPrice(currentPrice).savingPrice(savingPrice);
        }

        return amazonProduct;
    }
}
