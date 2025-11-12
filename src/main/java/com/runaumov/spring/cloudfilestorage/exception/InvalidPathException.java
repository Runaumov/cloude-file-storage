package com.runaumov.spring.cloudfilestorage.exception;

public class InvalidPathException extends CloudFileStorageApiException {
    public InvalidPathException(String message) {
        super(message);
    }
}
