package com.paperized.shopapi.scraper.impl;

import com.paperized.generated.shopapi.model.TrovaPrezziProduct;
import com.paperized.generated.shopapi.model.TrovaPrezziSort;
import com.paperized.shopapi.config.ScraperSettings;
import com.paperized.shopapi.dto.WebsiteSetting;
import com.paperized.shopapi.scraper.ScraperHttpService;
import com.paperized.shopapi.utils.ScraperUtils;
import com.paperized.shopapi.scraper.TrovaPrezziScraper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

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
    public List<TrovaPrezziProduct> searchProduct(String search, Integer category, Integer page, List<String> filters, Integer minPrice, Integer maxPrice, Integer rating, TrovaPrezziSort sort) throws HttpStatusException {
        String queryFilters = createQuery(search, category, page, filters, minPrice, maxPrice, rating, sort);
        String url = format("%s?%s", websiteSetting.getProp("searchUrl"), queryFilters);
        Document pageDoc = scraperHttpService.getPage(url, websiteSetting);
        logger.info("Scraping at url: {}", pageDoc.location());
        return scrapeSpecificCategory(pageDoc);
    }

    @Override
    public Map<String, List<String>> listCategories() {
        return null;
    }

    private List<TrovaPrezziProduct> scrapeSpecificCategory(Document page) {
        List<TrovaPrezziProduct> products = new ArrayList<>();
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
            products.add(new TrovaPrezziProduct()
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

    private String createQuery(String search, Integer category, Integer page, List<String> filters, Integer minPrice, Integer maxPrice, Integer rating, TrovaPrezziSort sort) {
        StringBuilder builder = new StringBuilder();
        if(StringUtils.isNotEmpty(search)) {
            newQueryParam(builder).append("libera=").append(search);
        }
        if(!CollectionUtils.isEmpty(filters)) {
            for(String filter : filters) {
                newQueryParam(builder).append("filtro%5B%5D=").append(filter);
            }
        }
        if(category != null) {
            newQueryParam(builder).append("id=").append(category);
        }
        if(page != null) {
            newQueryParam(builder).append("page=").append(page);
        }
        if(minPrice != null) {
            newQueryParam(builder).append("prezzomin=").append(minPrice);
        }
        if(maxPrice != null) {
            newQueryParam(builder).append("prezzomax=").append(maxPrice);
        }
        if(rating != null) {
            newQueryParam(builder).append("rating=").append(rating);
        }
        if(sort != null) {
            if(sort.equals(TrovaPrezziSort.CHEAPEST)) {
                newQueryParam(builder).append("sort=-prezzo");
            } else if(sort.equals(TrovaPrezziSort.UNPOPULAR)) {
                newQueryParam(builder).append("sort=-popolarita");
            }
        }

        return builder.toString();
    }

    private StringBuilder newQueryParam(StringBuilder builder) {
        if(!builder.isEmpty()) {
            builder.append("&");
        }
        return builder;
    }
}
