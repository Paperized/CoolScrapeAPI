package com.paperized.easynotifier.scraper.impl;

import com.paperized.generated.easynotifier.model.AmazonProductDto;
import com.paperized.easynotifier.config.ScraperSettings;
import com.paperized.easynotifier.exceptions.UnsuccessfulScrapeException;
import com.paperized.easynotifier.model.TrackerAction;
import com.paperized.easynotifier.model.WebsiteName;
import com.paperized.easynotifier.scraper.AmazonScraper;
import com.paperized.easynotifier.scraper.ScrapeExecutor;
import com.paperized.easynotifier.scraper.ScraperHttpService;
import com.paperized.easynotifier.scraper.annotations.ScrapeAction;
import com.paperized.easynotifier.utils.ScraperUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
public class AmazonScraperImpl extends ScrapeExecutor implements AmazonScraper {
    private final ScraperHttpService scraperHttpService;

    public AmazonScraperImpl(ScraperHttpService scraperHttpService, ScraperSettings scraperSettings) {
        super(scraperSettings, WebsiteName.Amazon);
        this.scraperHttpService = scraperHttpService;
    }

    @Override
    public AmazonProductDto findProductDetails(String url) throws HttpStatusException, UnsuccessfulScrapeException {
        return executeScrapeAction(TrackerAction.AMAZON_PRODUCT_DETAILS, url);
    }

    @ScrapeAction(action = TrackerAction.AMAZON_PRODUCT_DETAILS, retryTimes = 1, intervalRetry = 2000)
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

        amazonProduct.calculateAndSetUniqueIdentifier();
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
