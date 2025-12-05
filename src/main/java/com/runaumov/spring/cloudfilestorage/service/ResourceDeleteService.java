package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
import com.runaumov.spring.cloudfilestorage.util.MinioValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceDeleteService {
    private final MinioStorageService minioStorageService;
    private final AuthenticationService authenticationService;
    private final UserPathService userPathService;
    private final PathParserService pathParserService;


    public void deleteResource(String path) {
        Long userId = authenticationService.getCurrentUserId();
        String userPath = userPathService.addUserPrefix(userId, path);

        if (pathParserService.isDirectory(userPath)) {
            if (!userPath.equals(userPathService.getUserPrefix(userId))) {
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