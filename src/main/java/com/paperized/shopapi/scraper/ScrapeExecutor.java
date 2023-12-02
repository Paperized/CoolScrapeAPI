package com.paperized.shopapi.scraper;

import com.paperized.shopapi.exceptions.UnsuccessfulScrapeException;
import com.paperized.shopapi.model.TrackingAction;
import com.paperized.shopapi.model.WebsiteName;
import com.paperized.shopapi.scraper.annotations.ScrapeAction;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.HttpStatusException;
import org.slf4j.Logger;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public abstract class ScrapeExecutor {
    private String className;
    private final Map<TrackingAction, ScrapeFunction> actionFunctionMap = new HashMap<>();

    public ScrapeExecutor() {
        initializeActionFunctionMap();
    }

    public <T> T executeScrapeAction(TrackingAction action, String url) throws HttpStatusException, UnsuccessfulScrapeException {
        if(action == null) {
            throw new RuntimeException(format("(%s) Tracking action is null", className));
        }
        if(StringUtils.isBlank(url)) {
            throw new RuntimeException(format("(%s) Action (%s) required a not blank url", className, action));
        }

        ScrapeFunction fn = actionFunctionMap.get(action);
        if(fn == null) {
            throw new RuntimeException(format("Tracking action (%s) provided does not exist in this context (%s)", action, className));
        }

        //noinspection unchecked
        return (T) fn.call(url);
    }

    public abstract WebsiteName getWebsiteName();

    protected abstract Logger getLogger();

    private void initializeActionFunctionMap() {
        var clazz = getClass();
        className = clazz.getSimpleName();
        for (var method : clazz.getDeclaredMethods()) {
            ScrapeAction scrapeAction = method.getAnnotation(ScrapeAction.class);
            if(scrapeAction == null) continue;
            if(scrapeAction.action() == null) {
                throw new RuntimeException(format("%s.%s: no action specified", className, method.getName()));
            }
            if(method.getParameterCount() != 1) {
                throw new RuntimeException(format("%s.%s: paramters count must be 1", className, method.getName()));
            }
            if(!String.class.equals(method.getParameterTypes()[0])) {
                throw new RuntimeException(format("%s.%s: paramter must be an url string, found: %s", className, method.getName(), method.getParameterTypes()[0].getSimpleName()));
            }

            method.setAccessible(true);
            actionFunctionMap.put(scrapeAction.action(), getScrapeFunction(method, scrapeAction));
        }
    }

    private ScrapeFunction getScrapeFunction(Method method, ScrapeAction scrapeAction) {
        ScrapeFunction scrapeFunction;
        if(scrapeAction.retryTimes() > 0) {
            RetryTemplate retryTemplate = new RetryTemplate();

            FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
            fixedBackOffPolicy.setBackOffPeriod(scrapeAction.intervalRetry());
            retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

            SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
            retryPolicy.setMaxAttempts(scrapeAction.retryTimes() + 1);
            retryTemplate.setRetryPolicy(retryPolicy);

            scrapeFunction = (url) -> {
                try {
                    return retryTemplate.execute(s -> {
                        try {
                            Object res = method.invoke(this, url);
                            if (s.getRetryCount() > 0) {
                                Logger logger = getLogger();
                                if(logger != null) {
                                    logger.info("Scrape took {} tries!", s.getRetryCount());
                                }
                            }
                            return res;
                        } catch (InvocationTargetException e) {
                            Throwable target = e.getTargetException();
                            if (target instanceof HttpStatusException || target instanceof UnsuccessfulScrapeException)
                                throw (Exception) target;
                            throw e;
                        }
                    });
                } catch (HttpStatusException | UnsuccessfulScrapeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(format("%s.%s: %s", className, method.getName(), e.getMessage()), e);
                }
            };
        } else {
            scrapeFunction = (url) -> {
                try {
                    return method.invoke(this, url);
                } catch (InvocationTargetException e) {
                    Throwable target = e.getTargetException();
                    if (target instanceof HttpStatusException innerEx)
                        throw innerEx;
                    if (target instanceof UnsuccessfulScrapeException innerEx)
                        throw innerEx;

                    throw new RuntimeException(format("%s.%s: %s", className, method.getName(), e.getMessage()), e);
                } catch (Throwable e) {
                    throw new RuntimeException(format("%s.%s: %s", className, method.getName(), e.getMessage()), e);
                }
            };
        }
        return scrapeFunction;
    }

    @FunctionalInterface
    public interface ScrapeFunction {
        Object call(String url) throws HttpStatusException, UnsuccessfulScrapeException;
    }
}
