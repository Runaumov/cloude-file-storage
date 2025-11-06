package com.runaumov.spring.cloudfilestorage.exception;

public abstract class AbstractApiException extends RuntimeException {
    public AbstractApiException(String message) {
        super(message);
    }
}
