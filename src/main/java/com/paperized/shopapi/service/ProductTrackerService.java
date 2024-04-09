package com.paperized.shopapi.service;


import com.paperized.shopapi.model.webhookfilter.DQueryRequestWebhook;
import com.paperized.shopapi.model.TrackerAction;
import com.paperized.shopapi.model.WebsiteName;

public interface ProductTrackerService {
    String trackNewProduct(String url, WebsiteName websiteName, TrackerAction action, String webhookUrl, DQueryRequestWebhook queryRequestWebhook);
}
