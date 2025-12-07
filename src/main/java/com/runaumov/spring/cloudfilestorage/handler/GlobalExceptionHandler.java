package com.runaumov.spring.cloudfilestorage.handler;

import com.runaumov.spring.cloudfilestorage.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({InvalidPathException.class})
    public ResponseEntity<Void> handleInvalidPathException(InvalidPathException exception) {
        logger.warn("InvalidPathException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler({ResourceAlreadyExistsException.class})
    public ResponseEntity<Void> handleResourceAlreadyExistsException(ResourceAlreadyExistsException exception) {
        logger.warn("ResourceAlreadyExistsException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Void> handleResourceNotFoundException(ResourceNotFoundException exception) {
        logger.warn("ResourceNotFoundException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<Void> handleUsernameNotFoundException(UsernameNotFoundException exception) {
        logger.warn("UsernameNotFoundException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler({UsernameAlreadyExistException.class})
    public ResponseEntity<Void> handleUsernameAlreadyExists(UsernameAlreadyExistException exception) {
        logger.warn("UsernameAlreadyExistException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Void> handleConstraintViolation(ConstraintViolationException exception) {
        logger.warn("ConstraintViolationException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler({CloudFileStorageApiException.class})
    public ResponseEntity<Void> handleCloudFileStorageApiException(CloudFileStorageApiException exception) {
        logger.error("CloudFileStorageApiException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Void> handeUnexpectedException(Exception exception) {
        logger.error("Unexpected exception:", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}