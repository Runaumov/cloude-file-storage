package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.exception.CloudFileStorageApiException;
import com.runaumov.spring.cloudfilestorage.exception.ResourceNotFoundException;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioStorageService {

    private final MinioClient minioClient;
    @Value("${minio.bucket}")
    private String bucketName;

    public Iterable<Result<Item>> listDirectoryItems(String path, boolean recursive) {
        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(path)
                .recursive(recursive)
                .build());
    }

    public void putObject(String objectName, MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }
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

    public StatObjectResponse getStatObject(String path) throws Exception {
        return minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucketName)
                .object(path)
                .build());
    }

    public void deleteItemForPath(String path) {
        // TODO : переделать обработку исключений, возможно стоит пробросит в сервис для удаления
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(path)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении ресурса", e);
        }
    }

    public void copyObject(String to, String from) throws Exception {
        minioClient.copyObject(CopyObjectArgs.builder()
                .bucket(bucketName)
                .object(to)
                .source(CopySource.builder()
                        .bucket(bucketName)
                        .object(from)
                        .build())
                .build());
    }

    public InputStream getObjectStream(String objectName) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }
}