package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDirectoryDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDtoFactory;
import com.runaumov.spring.cloudfilestorage.exception.ResourceNotFoundException;
import com.runaumov.spring.cloudfilestorage.model.ResourceType;
import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
import com.runaumov.spring.cloudfilestorage.util.MinioValidator;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectoryService {

    private final MinioStorageService minioStorageService;
    private final PathParserService pathParserService;

    public List<ResourceResponseDto> getDirectoryInfo(String path) {
        String normalizedPath = pathParserService.normalizePath(path);

        if (!normalizedPath.isEmpty()) {
            MinioValidator.verificationDirectory(
                    normalizedPath,
                    () -> minioStorageService.getStatObject(normalizedPath),
                    () -> minioStorageService.listDirectoryItems(normalizedPath, false),
                    "Folder not found: " + path
            );
        }

        List<ResourceResponseDto> resources = new ArrayList<>();
        var results = minioStorageService.listDirectoryItems(normalizedPath, false);

        for (Result<Item> result : results) {
            Item item = MinioUtils.handleMinioException(result::get, "Failed to read directory item: " + normalizedPath);
            PathComponents itemPathComponents = pathParserService.parsePath(item.objectName());
            ResourceResponseDto resourceResponseDto = ResourceResponseDtoFactory.createDtoFromItemAndPathComponents(item, itemPathComponents);
            resources.add(resourceResponseDto);
        }
        return resources;
    }

    public ResourceResponseDto createEmptyDirectory(String path) {
        PathComponents pathComponents = pathParserService.parsePath(path);
        String folderName = pathComponents.path();

        // TODO : проверить

        MinioUtils.handleMinioException(() -> {
            minioStorageService.putEmptyItem(folderName);
            return null;
        }, "Failed to create directory: " + folderName);

        return ResourceResponseDtoFactory.createDtoFromPathComponents(pathComponents);
    }


}