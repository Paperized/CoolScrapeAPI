package com.paperized.easynotifier.exceptions;

public class TrackingExpiredException extends RuntimeException {
    public TrackingExpiredException() {
    }

    public TrackingExpiredException(String message) {
        super(message);
    }

    public TrackingExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public TrackingExpiredException(Throwable cause) {
        super(cause);
    }

    public TrackingExpiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}