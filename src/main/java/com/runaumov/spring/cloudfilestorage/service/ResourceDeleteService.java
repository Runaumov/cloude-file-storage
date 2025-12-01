package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
import com.runaumov.spring.cloudfilestorage.util.MinioValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceDeleteService {
    private final MinioStorageService minioStorageService;

    public void deleteResource(String path) {
        if (!path.isEmpty()) {
            MinioValidator.verificationDirectory(
                    path,
                    () -> minioStorageService.getStatObject(path),
                    () -> minioStorageService.listDirectoryItems(path, false),
                    "Folder not found: " + path
            );
        }

        MinioUtils.handleMinioException(() -> {
            if (path.endsWith("/")) { // TODO : переделать, подумать о централизованном методе
                minioStorageService.deletePathRecursive(path);
            } else {
                minioStorageService.deleteItemForPath(path);
            }
            return null; // TODO можно убрать null
        }, "Failed to delete resource: " + path);
    }
}