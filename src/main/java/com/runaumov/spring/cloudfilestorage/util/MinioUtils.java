package com.runaumov.spring.cloudfilestorage.util;

import com.runaumov.spring.cloudfilestorage.exception.CloudFileStorageApiException;
import com.runaumov.spring.cloudfilestorage.exception.ResourceNotFoundException;
import io.minio.errors.ErrorResponseException;

public class MinioUtils {

    public static <T> T handleMinioException(MinioCall<T> call, String errorMessage) {
        try {
            return call.execute();
        } catch (ErrorResponseException e) {
            throw new ResourceNotFoundException(errorMessage, e);
        } catch (Exception e) {
            throw new CloudFileStorageApiException(errorMessage, e);
        }
    }
}
