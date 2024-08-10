package com.paperized.easynotifier.service;

import com.paperized.easynotifier.exceptions.TrackingAlreadyScheduledException;

public interface ProductTrackerScheduler {
    void scheduleTracker(final String trackerId, boolean force) throws TrackingAlreadyScheduledException;
    void unscheduleTracker(String trackerId);
}
