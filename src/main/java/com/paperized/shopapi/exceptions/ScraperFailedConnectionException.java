package com.paperized.shopapi.exceptions;

public class ScraperFailedConnectionException extends RuntimeException {
    public ScraperFailedConnectionException() {
    }

    public ScraperFailedConnectionException(String message) {
        super(message);
    }

    public ScraperFailedConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScraperFailedConnectionException(Throwable cause) {
        super(cause);
    }

    public ScraperFailedConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
