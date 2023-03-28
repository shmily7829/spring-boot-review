package com.mily.springbootreview.exceptions;

public class DuplicateNumberException extends RuntimeException {
    public DuplicateNumberException() {
    }

    public DuplicateNumberException(String message) {
        super(message);
    }

    public DuplicateNumberException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateNumberException(Throwable cause) {
        super(cause);
    }

    public DuplicateNumberException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
