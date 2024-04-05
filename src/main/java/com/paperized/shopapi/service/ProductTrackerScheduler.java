package com.paperized.shopapi.service;

import com.paperized.shopapi.dto.DQueryRequestWebhook;
import com.paperized.shopapi.exceptions.TrackingAlreadyScheduledException;

public interface ProductTrackerScheduler {
    void scheduleTracker(final String trackerId, boolean force) throws TrackingAlreadyScheduledException;
    void unscheduleTracker(String trackerId);
}
