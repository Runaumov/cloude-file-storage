package com.runaumov.spring.cloudfilestorage;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bouncycastle.asn1.x509.NameConstraintValidator;

public class PathValidator implements ConstraintValidator<ValidPath, String> {

    private boolean required;
    private boolean allowEmpty;

    @Override
    public void initialize(ValidPath constraintAnnotation) {
        this.required = constraintAnnotation.required();
        this.allowEmpty = constraintAnnotation.allowEmpty();
    }

    @Override
    public boolean isValid(String path, ConstraintValidatorContext context) {

        if (path == null) {
            if (required) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Path is requred").addConstraintViolation();
                return false;
            }
            return true;
        }

        if (path.isEmpty()) {
            return allowEmpty;
        }

        if (path.contains("..")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Path cant contain '..' (path traversal)").addConstraintViolation();
            return false;
        }

        if (path.contains("\\")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Path cant contain backslashes")
                    .addConstraintViolation();
            return false;
        }

        if (path.contains("//")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Path cant contain double slashes")
                    .addConstraintViolation();
            return false;
        }

        if (path.startsWith("/")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Path cant start with '/'")
                    .addConstraintViolation();
            return false;
        }

        if (!path.matches("^[a-zA-Z0-9._/-]*$")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Path contains invalid characters")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
