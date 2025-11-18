package com.runaumov.spring.cloudfilestorage.exception;

public class ResourceNotFoundException extends CloudFileStorageApiException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
