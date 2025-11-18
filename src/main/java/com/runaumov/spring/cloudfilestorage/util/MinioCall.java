package com.runaumov.spring.cloudfilestorage.util;

@FunctionalInterface
public interface MinioCall<T> {
    T execute() throws Exception;
}
