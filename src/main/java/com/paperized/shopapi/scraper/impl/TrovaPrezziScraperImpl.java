package com.paperized.shopapi.scraper.impl;

import com.paperized.generated.shopapi.model.TrovaprezziProduct;
import com.paperized.shopapi.config.ScraperSettings;
import com.paperized.shopapi.dto.WebsiteSetting;
import com.paperized.shopapi.scraper.ScraperHttpService;
import com.paperized.shopapi.scraper.ScraperUtils;
import com.paperized.shopapi.scraper.TrovaPrezziScraper;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrovaPrezziScraperImpl implements TrovaPrezziScraper {
    Logger logger = LoggerFactory.getLogger(TrovaPrezziScraperImpl.class);
    private final ScraperHttpService scraperHttpService;
    private final WebsiteSetting websiteSetting;

    public TrovaPrezziScraperImpl(ScraperHttpService scraperHttpService, ScraperSettings scraperSettings) {
        this.scraperHttpService = scraperHttpService;
        websiteSetting = scraperSettings.fromName(WebsiteSetting.WebsiteName.TrovaPrezzi);
    }

    @Override
    public List<TrovaprezziProduct> searchProduct(String name) {
        String url = websiteSetting.getProp("urlFreeSearch");
        Document page = scraperHttpService.getPage(url + name, websiteSetting);
        if (page.location().startsWith(url)) {
            logger.info("No specific categegory found!");
            return scrapeGenericPage(page);
        } else {
            logger.info("Found specific category {}", page.location());
            return scrapeSpecificCategory(page);
        }
    }

    private List<TrovaprezziProduct> scrapeSpecificCategory(Document page) {
        List<TrovaprezziProduct> products = new ArrayList<>();
        Elements productElements = page.selectXpath("//*[@id='listing']//li[contains(@class, 'listing_item')]");
        for (Element el : productElements) {
            String imageUrl = ScraperUtils.getAttr(el.selectXpath(".//a[contains(@class, 'item_image')]/img").first(), "src"); // image
            Pair<String, String> linkAndName = ScraperUtils.get(el.selectXpath(".//a[contains(@class, 'item_name')]").first(),
                    e -> e.attr("href"),
                    Element::text);
            var description = ScraperUtils.getText(el.selectXpath(".//div[contains(@class, 'item_desc')]").first());
            var price = ScraperUtils.getText(el.selectXpath(".//div[contains(@class, 'item_basic_price')]").first());
            var priceDelivery = ScraperUtils.getText(el.selectXpath(".//div[contains(@class, 'item_delivery_price')]").first());
            var available = el.selectXpath(".//span[contains(@class, 'available')]").first() != null;
            products.add(new TrovaprezziProduct()
                    .name(linkAndName.getRight())
                    .description(description)
                    .imageUrl(imageUrl)
                    .url(linkAndName.getLeft())
                    .price(price)
                    .deliveryPrice(priceDelivery)
                    .available(available));
        }

        return products;
    }

    private List<TrovaprezziProduct> scrapeGenericPage(Document page) {
        return List.of();
    }
}
