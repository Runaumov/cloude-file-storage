package com.runaumov.spring.cloudfilestorage.util;

import com.runaumov.spring.cloudfilestorage.exception.CloudFileStorageApiException;
import com.runaumov.spring.cloudfilestorage.exception.InvalidPathException;
import com.runaumov.spring.cloudfilestorage.exception.ResourceNotFoundException;
import io.minio.errors.ErrorResponseException;

public class MinioUtils {

    public static <T> T handleMinioException(MinioCall<T> call, String errorMessage) {
        try {
            return call.execute();
        } catch (ErrorResponseException e) {
            String errorCode = e.errorResponse().code();

            if ("NoSuchKey".equals(errorCode)) {
                throw new ResourceNotFoundException(errorMessage + ": resource not found", e);
            }

            if ("InvalidArgument".equals(errorCode)) {
                throw new InvalidPathException(errorMessage + ": invalid path", e);
            }

            throw new CloudFileStorageApiException(errorMessage + ": MinioError: " + errorCode, e);

        } catch (Exception e) {
            throw new CloudFileStorageApiException(errorMessage + ": invalid path", e);
        }
    }
}
