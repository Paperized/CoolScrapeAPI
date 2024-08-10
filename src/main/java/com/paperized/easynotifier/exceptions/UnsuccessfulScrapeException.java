package com.paperized.easynotifier.exceptions;

public class UnsuccessfulScrapeException extends Exception {
    public UnsuccessfulScrapeException() {
    }

    public UnsuccessfulScrapeException(String message) {
        super(message);
    }

    public UnsuccessfulScrapeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsuccessfulScrapeException(Throwable cause) {
        super(cause);
    }

    public UnsuccessfulScrapeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
