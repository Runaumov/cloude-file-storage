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
    private final PathParserService pathParserService;

    public void deleteResource(String path) {
        MinioUtils.handleMinioException(() -> {
            if (isDirectory(path)) {
                String normalizePath = pathParserService.normalizePath(path);
                minioStorageService.deletePathRecursive(normalizePath);
            } else {
                minioStorageService.deleteItemForPath(path);
            }
            return null; //TODO: убрать null
        }, "Failed to delete resource: " + path);
    }

    private boolean isDirectory(String path) {
        return path.endsWith("/") || minioStorageService.listDirectoryItems(path, false).iterator().hasNext();
    }
}
