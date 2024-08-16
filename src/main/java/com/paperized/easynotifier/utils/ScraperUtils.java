package com.paperized.easynotifier.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.paperized.easynotifier.exceptions.UnsuccessfulScrapeException;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jsoup.nodes.Element;

import java.util.function.Function;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class ScraperUtils {

    public static String getText(Element element) {
        if(element == null) {
            return null;
        }
        return element.text();
    }

    public static String getAttr(Element element, String attr) {
        if(element == null) {
            return null;
        }
        return element.attr(attr);
    }

    public static String get(Element el, Function<Element, String> extractor) {
        if(el == null) {
            return null;
        }
        return extractor.apply(el);
    }

    @NotNull
    public static Pair<String, String> get(Element el, Function<Element, String> extractor1, Function<Element, String> extractor2) {
        if(el == null) {
            return new ImmutablePair<>(null, null);
        }
        return new ImmutablePair<>(extractor1.apply(el), extractor2.apply(el));
    }

    public static Triple<String, String, String> get(Element el, Function<Element, String> extractor1, Function<Element, String> extractor2, Function<Element, String> extractor3) {
        if(el == null) {
            return new ImmutableTriple<>(null, null, null);
        }
        return new ImmutableTriple<>(extractor1.apply(el), extractor2.apply(el), extractor3.apply(el));
    }

    public static <T> T getData(Supplier<T> supplier) {
        return getData(supplier, null);
    }

    public static <T> T getData(Supplier<T> supplier, String logNotFound) {
        try {
            return supplier.get();
        } catch (Exception ex) {
            if(StringUtils.isNotBlank(logNotFound)) {
                log.info(logNotFound);
            }

            return null;
        }
    }

    public static <T extends JsonNode> T getNode(Supplier<T> supplier, String logNotFound) {
        try {
            return supplier.get();
        } catch (Exception ex) {
            log.info(logNotFound);
            return null;
        }
    }

    public static <T extends JsonNode> T getNodeAndThrow(Supplier<T> supplier, String logNotFound) throws UnsuccessfulScrapeException {
        try {
            return supplier.get();
        } catch (Exception ex) {
            log.info(logNotFound);
            throw new UnsuccessfulScrapeException(logNotFound, ex);
        }
    }
}
