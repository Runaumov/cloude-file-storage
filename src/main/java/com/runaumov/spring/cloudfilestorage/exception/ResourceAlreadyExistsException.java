package com.runaumov.spring.cloudfilestorage.exception;

public class ResourceAlreadyExistsException extends CloudFileStorageApiException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
