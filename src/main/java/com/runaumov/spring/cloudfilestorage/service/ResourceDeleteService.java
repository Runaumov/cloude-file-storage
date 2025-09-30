package com.runaumov.spring.cloudfilestorage.service;

import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceDeleteService {

    private final MinioClient minioClient;
    @Value("${minio.bucket}")
    private String bucketName;

    public void deleteResource(String path) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(path)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении ресурса", e);
        }
    }
}
