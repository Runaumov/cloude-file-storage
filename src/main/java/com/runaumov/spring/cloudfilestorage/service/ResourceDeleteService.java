package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
import com.runaumov.spring.cloudfilestorage.util.MinioValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceDeleteService {
    private final MinioStorageService minioStorageService;
    private final PathParserService pathParserService;
    private final UserContextService userContextService;


    public void deleteResource(String path) {
        String userPath = userContextService.addUserPrefix(path);

        if (pathParserService.isDirectory(userPath)) {
            if (!userContextService.isUserRoot(userPath)) {
                MinioValidator.verificationDirectory(
                        userPath,
                        () -> minioStorageService.getStatObject(userPath),
                        () -> minioStorageService.listDirectoryItems(userPath, false),
                        "Folder not found: " + path
                );
            }
        } else {
            MinioUtils.handleMinioException(() -> minioStorageService.getStatObject(userPath), "File not found: " + path);
        }

        MinioUtils.handleMinioException(() -> {
            if (pathParserService.isDirectory(userPath)) {
                minioStorageService.deletePathRecursive(userPath);
            } else {
                minioStorageService.deleteItemForPath(userPath);
            }
            return null; // TODO можно убрать null
        }, "Failed to delete resource: " + path);
    }
}