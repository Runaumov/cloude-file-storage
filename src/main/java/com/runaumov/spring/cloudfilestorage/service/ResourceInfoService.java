package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDtoFactory;
import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
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
        String folderName = pathComponents.path();

        StatObjectResponse statObject = MinioUtils.handleMinioException(
                () -> minioStorageService.getStatObject(folderName),
                "Resource not found: " + path
        );

        if (isDirectoryNew(statObject)) {
            return ResourceResponseDtoFactory.createDtoFromPathComponents(pathComponents);
        } else {
            return ResourceResponseDtoFactory.createDtoFromStatObject(statObject, pathComponents);
        }
    }

    // TODO : rename
    private boolean isDirectoryNew(StatObjectResponse statObject) {
        return "application/x-directory".equals(statObject.contentType())
                || statObject.object().endsWith("/");
    }
}