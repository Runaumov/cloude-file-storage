package com.runaumov.spring.cloudfilestorage.exception;

public class UserNotFoundException extends CloudFileStorageApiException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
