package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectResponse;
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
            if (path.endsWith("/")) {
                minioStorageService.deletePathRecursive(path);
            } else {
                minioStorageService.deleteItemForPath(path);
            }
            return null;
        }, "Failed to delete resource: " + path);
    }

    // TODO
    private boolean isDirectoryN(StatObjectResponse statObject) {
        return "application/x-directory".equals(statObject.contentType())
                || statObject.object().endsWith("/");
    }
}
