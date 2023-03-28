package com.mily.springbootreview.exceptions;

public class StateFormatException extends RuntimeException{
    public StateFormatException() {
    }

    public StateFormatException(String message) {
        super(message);
    }

    public StateFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public StateFormatException(Throwable cause) {
        super(cause);
    }

    public StateFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
