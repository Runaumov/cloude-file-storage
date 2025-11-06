package com.runaumov.spring.cloudfilestorage.exception;

public class UserNotFoundException extends AbstractApiException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
