package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDtoFactory;
import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceSearchService {
    private final MinioStorageService minioStorageService;
    private final PathParserService pathParserService;

    public ResourceResponseDto searchResource(String query) {
        return MinioUtils.handleMinioException(() -> {
            StatObjectResponse statObject = minioStorageService.getStatObject(query);
            PathComponents pathComponents = pathParserService.parsePath(query);
            return ResourceResponseDtoFactory.createDtoFromStatObject(statObject, pathComponents);
        }, "Failed to search file: " + query);

    }
}