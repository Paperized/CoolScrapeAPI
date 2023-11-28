package com.paperized.shopapi.scraper.impl;

import com.paperized.generated.shopapi.model.AmazonProduct;
import com.paperized.shopapi.config.ScraperSettings;
import com.paperized.shopapi.dto.WebsiteSetting;
import com.paperized.shopapi.scraper.AmazonScraper;
import com.paperized.shopapi.scraper.ScraperHttpService;
import com.paperized.shopapi.utils.ScraperUtils;
import org.apache.commons.lang3.StringUtils;
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

        if (extractDetailsRedOfferPanel(page, amazonProduct))
            return amazonProduct;

        // this is used for both discounted products and normal products since they have both a currentPrice common field
        extractDetailsOfferPanel(page, amazonProduct);
        return amazonProduct;
    }

    private boolean extractDetailsRedOfferPanel(Document page, AmazonProduct amazonProduct) {
        var pricePanel = page.selectXpath("//*[starts-with(@id, 'corePrice_desktop')]").first();
        if (pricePanel != null) {
            var lastPrice = ScraperUtils.getText(pricePanel.selectXpath(".//*[@data-a-strike='true']/*[1]").first());
            var currentPrice = ScraperUtils.getText(pricePanel.selectXpath(".//*[@id='taxInclusiveMessage']/../*[@data-a-color='price']/*[1]").first());
            var savingPrice = ScraperUtils.getText(pricePanel.selectXpath("//*[contains(@id, 'bundleLTBSSavings')]/..//*[@data-a-color='price']/*[1]").first());
            amazonProduct.lastPrice(lastPrice).currentPrice(currentPrice).savingPrice(savingPrice);
        }

        return StringUtils.isNoneEmpty(amazonProduct.getLastPrice(), amazonProduct.getCurrentPrice(), amazonProduct.getSavingPrice());
    }

    private void extractDetailsOfferPanel(Document page, AmazonProduct amazonProduct) {
        var pricePanel = page.selectXpath("//*[starts-with(@id, 'corePriceDisplay')]").first();
        if (pricePanel != null) {
            var currentPrice = ScraperUtils.getText(pricePanel.selectXpath(".//*[@id='taxInclusiveMessage']/../span[contains(@class, 'priceToPay')]").first());
            var suggestedPrice = ScraperUtils.getText(pricePanel.selectXpath(".//span[contains(@class, 'basisPrice')]//span[@data-a-strike]/*[1]").first());
            amazonProduct.currentPrice(currentPrice).suggestedPrice(suggestedPrice);
        }
    }
}
