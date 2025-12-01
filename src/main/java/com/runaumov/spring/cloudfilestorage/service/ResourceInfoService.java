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

    public ResourceResponseDto getResourceInfo(String path) {
        PathComponents pathComponents = pathParserService.parsePath(path);

        if (pathParserService.isDirectory(path)) {
            MinioValidator.verificationDirectory(
                    path,
                    () -> minioStorageService.getStatObject(path),
                    () -> minioStorageService.listDirectoryItems(path, false),
                    "Folder not found: " + path
            );
            return ResourceResponseDtoFactory.createDtoFromPathComponents(pathComponents);
        } else {
            StatObjectResponse statObject = MinioUtils.handleMinioException(
                    () -> minioStorageService.getStatObject(path),
                    "File not found: " + path
            );
            return ResourceResponseDtoFactory.createDtoFromStatObject(statObject, pathComponents);
        }
    }
}