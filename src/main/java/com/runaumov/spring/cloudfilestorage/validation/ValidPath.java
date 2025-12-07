package com.runaumov.spring.cloudfilestorage.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PathValidator.class)
public @interface ValidPath {
    String message() default "Invalid path";
    Class<?>[] groups() default {};
    Class<? extends Payload> [] payload() default {};

    boolean required() default true;
    boolean allowEmpty() default false;
}
