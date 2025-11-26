package com.runaumov.spring.cloudfilestorage.exception;

public class UsernameAlreadyExistException extends CloudFileStorageApiException {

    public UsernameAlreadyExistException(String message) {
        super(message);
    }

    public UsernameAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
