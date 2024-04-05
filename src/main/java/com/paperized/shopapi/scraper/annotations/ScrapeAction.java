package com.paperized.shopapi.scraper.annotations;

import com.paperized.shopapi.model.TrackerAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ScrapeAction {
    TrackerAction action();
    int retryTimes() default 0;
    int intervalRetry() default 1000;
}
