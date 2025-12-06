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
    private final PathParserService pathParserService;
    private final UserContextService userContextService;

    public ResourceResponseDto getResourceInfo(String path) {

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

            String pathWithoutPrefix = userContextService.removeUserPrefix(userPath);
            PathComponents pathComponents = pathParserService.parsePath(pathWithoutPrefix);
            return ResourceResponseDtoFactory.createDtoFromPathComponents(pathComponents);
        } else {
            StatObjectResponse statObject = MinioUtils.handleMinioException(
                    () -> minioStorageService.getStatObject(userPath),
                    "File not found: " + path
            );
            String pathWithoutPrefix = userContextService.removeUserPrefix(userPath);
            PathComponents pathComponents = pathParserService.parsePath(pathWithoutPrefix);

            return ResourceResponseDtoFactory.createDtoFromStatObject(statObject, pathComponents);
        }
    }
}