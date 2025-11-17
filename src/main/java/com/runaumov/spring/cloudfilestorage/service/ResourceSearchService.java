package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDtoFactory;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceSearchService {

    private final MinioStorageService minioStorageService;
    private final PathParserService pathParserService;

    private final MinioClient minioClient;
    @Value("${minio.bucket}")
    private String bucketName;

    public ResourceResponseDto searchResource(String query) {
        try {
            StatObjectResponse statObject = minioStorageService.getStatObject(query);
            PathComponents pathComponents = pathParserService.parsePath(query);
            return ResourceResponseDtoFactory.createDtoFromStatObject(statObject, pathComponents);
        } catch (Exception e) {
            throw new RuntimeException("Файл не найден", e);
        }
    }
}