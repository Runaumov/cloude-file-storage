package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDtoFactory;
import com.runaumov.spring.cloudfilestorage.exception.CloudFileStorageApiException;
import com.runaumov.spring.cloudfilestorage.exception.ResourceNotFoundException;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectoryService {

    private final MinioClient minioClient;
    private final MinioStorageService minioStorageService;
    private final PathParserService pathParserService;
    @Value("${minio.bucket}")
    private String bucketName;

    public List<ResourceResponseDto> getDirectoryInfo(String path) {
        List<ResourceResponseDto> resources = new ArrayList<>();

        String prefix = path.endsWith("/") ? path : path + "/";

        var results = minioStorageService.listDirectoryItems(prefix);

        for (Result<Item> result : results) {
            try {
                Item item = result.get(); // TODO : Написать нормальную обработку ошибок
                PathComponents pathComponents = pathParserService.parsePath(item.objectName());
                ResourceResponseDto resourceResponseDto = ResourceResponseDtoFactory.createDtoFromItemAndPathComponents(item, pathComponents);
                resources.add(resourceResponseDto);
            } catch (ErrorResponseException e) {
                throw new ResourceNotFoundException("Directory not found: " + path + e);
            } catch (RuntimeException e) {
                throw new CloudFileStorageApiException("Unknown error" + e);
            } catch (Exception e) {
                throw new CloudFileStorageApiException("???" + e);
            }
        }

        return resources;
    }

    public ResourceResponseDto createEmptyDirectory(String path) {
        String normalPath = pathParserService.normalizePath(path);
        PathComponents pathComponents = pathParserService.parsePath(path);

        try {
            minioStorageService.putEmptyItem(normalPath);
        } catch (Exception e) {
            // TODO : написать обработку исключений
            throw new CloudFileStorageApiException("");
        }

        return ResourceResponseDtoFactory.createDtoFromPathComponents(pathComponents);
    }

}