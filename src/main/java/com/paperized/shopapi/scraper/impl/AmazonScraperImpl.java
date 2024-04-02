package com.paperized.shopapi.scraper.impl;

import com.paperized.generated.shopapi.model.AmazonProductDto;
import com.paperized.shopapi.config.ScraperSettings;
import com.paperized.shopapi.dto.WebsiteSetting;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import com.paperized.shopapi.model.TrackingAction;
import com.paperized.shopapi.model.WebsiteName;
import com.paperized.shopapi.scraper.AmazonScraper;
import com.paperized.shopapi.scraper.ScrapeExecutor;
import com.paperized.shopapi.scraper.ScraperHttpService;
import com.paperized.shopapi.scraper.annotations.ScrapeAction;
import com.paperized.shopapi.utils.ScraperUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AmazonScraperImpl extends ScrapeExecutor implements AmazonScraper {
    private final ScraperHttpService scraperHttpService;

    public AmazonScraperImpl(ScraperHttpService scraperHttpService, ScraperSettings scraperSettings) {
        super(scraperSettings, WebsiteName.Amazon);
        this.scraperHttpService = scraperHttpService;
    }

    @Override
    public AmazonProductDto findProductDetails(String url) throws HttpStatusException, UnsuccessfulScrapeException {
        return executeScrapeAction(TrackingAction.AMAZON_PRODUCT_DETAILS, url);
    }

    @ScrapeAction(action = TrackingAction.AMAZON_PRODUCT_DETAILS, retryTimes = 1, intervalRetry = 2000)
    protected AmazonProductDto findProductDetailsInternal(String url) throws HttpStatusException, UnsuccessfulScrapeException {
        AmazonProductDto amazonProduct = new AmazonProductDto();
        Document page = scraperHttpService.getPage(url, websiteSetting);
        logger.info("Loaded amazon page: {}", page.location());

        amazonProduct.setName(ScraperUtils.getText(page.getElementById("productTitle")));

        if (extractDetailsRedOfferPanel(page, amazonProduct)) {
            return amazonProduct;
        }

        // this is used for both discounted products and normal products since they have both a currentPrice common field
        if(!extractDetailsOfferPanel(page, amazonProduct)) {
            throw new UnsuccessfulScrapeException("Page loaded but unable to scrape data from url: " + url);
        }

        return amazonProduct;
    }

    private boolean extractDetailsRedOfferPanel(Document page, AmazonProductDto amazonProduct) {
        var pricePanel = page.selectXpath("//*[starts-with(@id, 'corePrice_desktop')]").first();
        if (pricePanel != null) {
            var lastPrice = ScraperUtils.getText(pricePanel.selectXpath(".//*[@data-a-strike='true']/*[1]").first());
            var currentPrice = ScraperUtils.getText(pricePanel.selectXpath(".//*[@id='taxInclusiveMessage']/../*[@data-a-color='price']/*[1]").first());
            var savingPrice = ScraperUtils.getText(pricePanel.selectXpath("//*[contains(@id, 'bundleLTBSSavings')]/..//*[@data-a-color='price']/*[1]").first());
            amazonProduct
                    .lastPrice(tryConvertPriceToDouble(lastPrice))
                    .currentPrice(tryConvertPriceToDouble(currentPrice))
                    .savingPrice(tryConvertPriceToDouble(savingPrice));
        }

        return ObjectUtils.allNotNull(amazonProduct.getLastPrice(), amazonProduct.getCurrentPrice(), amazonProduct.getSavingPrice());
    }

    private boolean extractDetailsOfferPanel(Document page, AmazonProductDto amazonProduct) {
        var pricePanel = page.selectXpath("//*[starts-with(@id, 'corePriceDisplay')]").first();
        if (pricePanel != null) {
            String currentPrice = ScraperUtils.getText(pricePanel.selectXpath(".//*[@id='taxInclusiveMessage']/../span[contains(@class, 'priceToPay')]/*[@aria-hidden='true']").first());
            var suggestedPrice = ScraperUtils.getText(pricePanel.selectXpath(".//span[contains(@class, 'basisPrice')]//span[@data-a-strike]/*[1]").first());
            amazonProduct
                    .currentPrice(tryConvertPriceToDouble(currentPrice))
                    .suggestedPrice(tryConvertPriceToDouble(suggestedPrice));
        }

        // at least current price is needed to be a successful scrape
        return amazonProduct.getCurrentPrice() != null;
    }

    private Double tryConvertPriceToDouble(String price) {
        if(StringUtils.isBlank(price)) {
            return null;
        }

        try {
            return Double.parseDouble(price.replace(',', '.').substring(0, price.length() - 1));
        } catch (NumberFormatException ex) {
            logger.warn(ex.toString());
            return null;
        }
    }
}
