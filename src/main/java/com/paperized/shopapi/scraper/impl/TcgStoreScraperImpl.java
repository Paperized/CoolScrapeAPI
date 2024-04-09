package com.paperized.shopapi.scraper.impl;

import com.paperized.generated.shopapi.model.TcgProductDto;
import com.paperized.shopapi.config.ScraperSettings;
import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import com.paperized.shopapi.model.TrackerAction;
import com.paperized.shopapi.model.WebsiteName;
import com.paperized.shopapi.scraper.ScrapeExecutor;
import com.paperized.shopapi.scraper.ScraperHttpService;
import com.paperized.shopapi.scraper.TcgStoreScraper;
import com.paperized.shopapi.scraper.annotations.ScrapeAction;
import com.paperized.shopapi.utils.ScraperUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TcgStoreScraperImpl extends ScrapeExecutor implements TcgStoreScraper {
    private final ScraperHttpService scraperHttpService;

    public TcgStoreScraperImpl(ScraperHttpService scraperHttpService, ScraperSettings scraperSettings) {
        super(scraperSettings, WebsiteName.TcgStore);
        this.scraperHttpService = scraperHttpService;
    }

    @Override
    public List<TcgProductDto> findSummaryAllProducts() throws HttpStatusException, UnsuccessfulScrapeException {
        String url = getSummaryProductsUrl(1);
        if(StringUtils.isBlank(url)) {
            logger.error("Find Summary Products url not found");
            throw new NullPointerException("Find Summary Products url not found");
        }

        return executeScrapeAction(TrackerAction.TCGSTORE_ALL_SUMMARY_PRODUCTS, url);
    }

    @ScrapeAction(action = TrackerAction.TCGSTORE_ALL_SUMMARY_PRODUCTS, retryTimes = 1, intervalRetry = 2000)
    protected List<TcgProductDto> findSummaryAllProductsInternal(String url) throws HttpStatusException, UnsuccessfulScrapeException {
        int currPage = -1;
        int nextPage = 1;
        List<TcgProductDto> tcgProductsDto = new ArrayList<>();

        while(currPage != nextPage) {
            currPage = nextPage;

            Document page = scraperHttpService.getPage(getSummaryProductsUrl(currPage), websiteSetting);
            logger.info("Loaded tcg store page: {}", page.location());

            Elements boxes = page.selectXpath("//*[contains(@class, 'product-small box')]");
            for(Element el : boxes) {
                TcgProductDto currDto = extractSummaryProductDto(el);
                currDto.calculateAndSetUniqueIdentifier();
                tcgProductsDto.add(currDto);
            }

            Element nextPageEl = page.selectXpath("//*[@class='woocommerce-pagination']//*[@aria-current='page']/../following-sibling::li[1]").first();
            if(nextPageEl != null) {
                try {
                    nextPage = Integer.parseInt(nextPageEl.text());
                } catch (Exception e) {
                    logger.error("Finishing tcg store all summary because couldnt convert next page str to integer: " + nextPageEl.text());
                }
            }
        }

        return tcgProductsDto;
    }

    @Override
    public List<TcgProductDto> findSummaryProducts(int page) throws HttpStatusException, UnsuccessfulScrapeException {
        String url = getSummaryProductsUrl(page);
        if(StringUtils.isBlank(url)) {
            logger.error("Find Summary Products url not found");
            throw new NullPointerException("Find Summary Products url not found");
        }

        return executeScrapeAction(TrackerAction.TCGSTORE_SUMMARY_PRODUCTS, url);
    }

    @ScrapeAction(action = TrackerAction.TCGSTORE_SUMMARY_PRODUCTS, retryTimes = 1, intervalRetry = 2000)
    protected List<TcgProductDto> findSummaryProductsInternal(String url) throws HttpStatusException, UnsuccessfulScrapeException {
        List<TcgProductDto> tcgProductsDto = new ArrayList<>();
        Document page = scraperHttpService.getPage(url, websiteSetting);
        logger.info("Loaded tcg store page: {}", page.location());

        Elements boxes = page.selectXpath("//*[contains(@class, 'product-small box')]");
        for(Element el : boxes) {
            TcgProductDto currDto = extractSummaryProductDto(el);
            currDto.calculateAndSetUniqueIdentifier();
            tcgProductsDto.add(currDto);
        }

        return tcgProductsDto;
    }

    private TcgProductDto extractSummaryProductDto(Element el) {
        String imgUrl = ScraperUtils.getAttr(el.selectXpath(".//*[contains(@class, 'image-fade_in_back')]//img").first(), "src");
        boolean soldOut = !el.selectXpath(".//*[contains(@class, 'out-of-stock-label')]").isEmpty();
        String category = ScraperUtils.getText(el.selectXpath(".//*[@class='title-wrapper']/*[contains(@class, 'product-cat')]").first());

        Element nameLink = el.selectXpath(".//*[@class='title-wrapper']//*[contains(@class, 'product__link')]").first();
        String name = ScraperUtils.getText(nameLink);
        String link = ScraperUtils.getAttr(nameLink, "href");

        String oldPrice = ScraperUtils.getText(el.selectXpath(".//*[@class='price']/del//bdi").first());
        String newPrice;
        if(StringUtils.isBlank(oldPrice)) {
            oldPrice = ScraperUtils.getText(el.selectXpath(".//*[@class='price']//bdi").first());
            newPrice = oldPrice;
        } else {
            newPrice = ScraperUtils.getText(el.selectXpath(".//*[@class='price']/ins//bdi").first());
        }

        return new TcgProductDto()
                .name(name)
                .url(link)
                .soldOut(soldOut)
                .category(category)
                .img(imgUrl)
                .oldPrice(StringUtils.isBlank(oldPrice) ? null : tryConvertPriceToDouble(oldPrice.substring(1)))
                .newPrice(StringUtils.isBlank(newPrice) ? null : tryConvertPriceToDouble(newPrice.substring(1)));
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

    @Override
    public String getSummaryProductsUrl(Integer page) {
        String url = websiteSetting.getCustomProperties().getOrDefault("find-summary-products", null);
        if(url != null) {
            if(page < 1) page = 1;
            url += "?product-page=" + page;
        }

        return url;
    }
}
