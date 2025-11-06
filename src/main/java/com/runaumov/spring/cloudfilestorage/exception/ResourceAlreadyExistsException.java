package com.runaumov.spring.cloudfilestorage.exception;

public class ResourceAlreadyExistsException extends AbstractApiException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
