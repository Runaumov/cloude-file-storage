package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceDeleteService {
    private final MinioStorageService minioStorageService;

    public void deleteResource(String path) {
        MinioUtils.handleMinioException(() -> {
            minioStorageService.deleteItemForPath(path);
            return null; //TODO: убрать null
        }, "Failed to delete resource: " + path);
    }
}
