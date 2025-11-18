package com.runaumov.spring.cloudfilestorage.exception;

public class CloudFileStorageApiException extends RuntimeException {
    public CloudFileStorageApiException(String message) {
        super(message);
    }

    public CloudFileStorageApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
