package com.runaumov.spring.cloudfilestorage.exception;

public class UserUnautorizedException extends CloudFileStorageApiException {
    public UserUnautorizedException(String message) {
        super(message);
    }
}
