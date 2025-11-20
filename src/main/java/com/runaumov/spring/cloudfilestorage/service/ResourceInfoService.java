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

        return MinioUtils.handleMinioException(() -> {
            StatObjectResponse statObject = minioStorageService.getStatObject(path);
            return ResourceResponseDtoFactory.createDtoFromStatObject(statObject, pathComponents);
        }, "Failed to get resource info");

    }
}