package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDtoFactory;
import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceMoveService {
    private final MinioStorageService minioStorageService;
    private final PathParserService pathParserService;

    public ResourceResponseDto resourceMove(String from, String to) {
        return MinioUtils.handleMinioException(() -> {
            minioStorageService.copyObject(to, from);
            minioStorageService.deleteItemForPath(from);

            StatObjectResponse statObject1 = minioStorageService.getStatObject(to);
            PathComponents pathComponents = pathParserService.parsePath(to);

            return ResourceResponseDtoFactory.createDtoFromStatObject(statObject1, pathComponents);
        }, "Failed to move resources from: " + from + " to: " + to);
    }
}
