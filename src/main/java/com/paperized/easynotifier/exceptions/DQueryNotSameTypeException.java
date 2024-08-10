package com.paperized.easynotifier.exceptions;

public class DQueryNotSameTypeException extends RuntimeException {
    public DQueryNotSameTypeException() {
    }

    public DQueryNotSameTypeException(String message) {
        super(message);
    }

    public DQueryNotSameTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DQueryNotSameTypeException(Throwable cause) {
        super(cause);
    }

    public DQueryNotSameTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
