package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.exception.CloudFileStorageApiException;
import com.runaumov.spring.cloudfilestorage.exception.ResourceNotFoundException;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioStorageService {

    private final MinioClient minioClient;
    @Value("${minio.bucket}")
    private String bucketName;

    public Iterable<Result<Item>> listDirectoryItems(String path) {
        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(path)
                .recursive(false)
                .build());
    }

    public void putEmptyItem(String path) throws Exception {
        byte[] empty = new byte[0];
        try (InputStream inputStream = new ByteArrayInputStream(empty)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(path)
                    .stream(inputStream, 0, -1)
                    .contentType("application/x-directory")
                    .build());
        }
    }
}
