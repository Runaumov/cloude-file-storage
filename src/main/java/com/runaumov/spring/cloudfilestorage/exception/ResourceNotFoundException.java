package com.runaumov.spring.cloudfilestorage.exception;

public class ResourceNotFoundException extends CloudFileStorageApiException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
