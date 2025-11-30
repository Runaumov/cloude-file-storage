package com.runaumov.spring.cloudfilestorage.util;

import com.runaumov.spring.cloudfilestorage.exception.ResourceNotFoundException;
import io.minio.Result;
import io.minio.messages.Item;

public class MinioValidator {

    public static void verificationDirectory(String path, MinioCall<?> statObjectCall, MinioCall<Iterable<Result<Item>>> listItemsCall, String errorMessage) {
        try {
            MinioUtils.handleMinioException(statObjectCall, errorMessage);
        } catch (ResourceNotFoundException e) {
            Iterable<Result<Item>> items = MinioUtils.handleMinioException(listItemsCall, errorMessage);
            if (!items.iterator().hasNext()) {
                throw new ResourceNotFoundException(errorMessage);
            }
        }
    }
}
