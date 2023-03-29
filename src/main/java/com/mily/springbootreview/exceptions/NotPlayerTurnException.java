package com.mily.springbootreview.exceptions;

public class NotPlayerTurnException extends ServiceException{
    public NotPlayerTurnException() {
    }

    public NotPlayerTurnException(String message) {
        super(message);
    }

    public NotPlayerTurnException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotPlayerTurnException(Throwable cause) {
        super(cause);
    }

    public NotPlayerTurnException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
