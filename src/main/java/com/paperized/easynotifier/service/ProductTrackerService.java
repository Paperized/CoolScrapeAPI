package com.paperized.easynotifier.service;


import com.paperized.easynotifier.dto.TrackListeningDto;
import com.paperized.easynotifier.model.webhookfilter.DQueryRequestScheduled;
import com.paperized.easynotifier.model.TrackerAction;
import com.paperized.easynotifier.model.WebsiteName;

public interface ProductTrackerService {
    String trackNewProduct(String url, WebsiteName websiteName, TrackerAction action, TrackListeningDto trackListeningDto);
    boolean existsTrackerIdWithWS(String trackerId);
}
