package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDtoFactory;
import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
import com.runaumov.spring.cloudfilestorage.util.MinioValidator;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceInfoService {
    private final MinioStorageService minioStorageService;
    private final AuthenticationService authenticationService;
    private final UserPathService userPathService;
    private final PathParserService pathParserService;

    public ResourceResponseDto getResourceInfo(String path) {

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

            String pathWithoutPrefix = userPathService.removeUserPrefix(userId, userPath);
            PathComponents pathComponents = pathParserService.parsePath(pathWithoutPrefix);
            return ResourceResponseDtoFactory.createDtoFromPathComponents(pathComponents);
        } else {
            StatObjectResponse statObject = MinioUtils.handleMinioException(
                    () -> minioStorageService.getStatObject(userPath),
                    "File not found: " + path
            );
            String pathWithoutPrefix = userPathService.removeUserPrefix(userId, userPath);
            PathComponents pathComponents = pathParserService.parsePath(pathWithoutPrefix);

            return ResourceResponseDtoFactory.createDtoFromStatObject(statObject, pathComponents);
        }
    }
}