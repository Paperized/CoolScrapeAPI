package com.paperized.easynotifier.exceptions;

public class NoDataException extends RuntimeException {
    public NoDataException() {
        super();
    }

    public NoDataException(String message) {
        super(message);
    }

    public NoDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoDataException(Throwable cause) {
        super(cause);
    }

    protected NoDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
