package com.paperized.easynotifier.service;


import com.paperized.easynotifier.model.webhookfilter.DQueryRequestWebhook;
import com.paperized.easynotifier.model.TrackerAction;
import com.paperized.easynotifier.model.WebsiteName;

public interface ProductTrackerService {
    String trackNewProduct(String url, WebsiteName websiteName, TrackerAction action, String webhookUrl, DQueryRequestWebhook queryRequestWebhook);
}
