package com.runaumov.spring.cloudfilestorage.exception;

public class ResourceNotFoundException extends AbstractApiException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
