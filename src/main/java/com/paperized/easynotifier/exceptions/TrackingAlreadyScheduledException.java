package com.paperized.easynotifier.exceptions;

public class TrackingAlreadyScheduledException extends Exception {
    public TrackingAlreadyScheduledException() {
    }

    public TrackingAlreadyScheduledException(String message) {
        super(message);
    }

    public TrackingAlreadyScheduledException(String message, Throwable cause) {
        super(message, cause);
    }

    public TrackingAlreadyScheduledException(Throwable cause) {
        super(cause);
    }

    public TrackingAlreadyScheduledException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
