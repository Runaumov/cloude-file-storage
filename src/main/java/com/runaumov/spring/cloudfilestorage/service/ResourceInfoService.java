package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDtoFactory;
import io.minio.MinioClient;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceInfoService {

    private final MinioClient minioClient;
    private final MinioStorageService minioStorageService;
    private final PathParserService pathParserService;

    public ResourceResponseDto getResourceInfo(String path) {
        PathComponents pathComponents = pathParserService.parsePath(path);

        try {
            StatObjectResponse statObject = minioStorageService.getStatObject(path);
            return ResourceResponseDtoFactory.createDtoFromStatObject(statObject, pathComponents);
        } catch (Exception e) {
            //TODO : обработать исключения
            throw new RuntimeException("Ошибка при получении информации о директории", e);
        }
    }
}
