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
    private final MinioStorageService minioStorageService;
    @Value("${minio.bucket}")
    private String bucketName;

    public void deleteResource(String path) {
        minioStorageService.deleteItemForPath(path);
    }
}
