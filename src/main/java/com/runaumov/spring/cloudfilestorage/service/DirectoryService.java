package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDirectoryDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDtoFactory;
import com.runaumov.spring.cloudfilestorage.exception.ResourceAlreadyExistsException;
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
        String normalizedPath = pathParserService.normalizePath(path); // tODO возможно стоит убрать, если мы исключаем отправку некорректного пути

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

        if (checkDirectoryExist(path)) {
            throw new ResourceAlreadyExistsException("Resource already exists: " + path);
        }

        PathComponents pathComponents = pathParserService.parsePath(path);
        String parentPath = pathComponents.path();

        if (!parentPath.isEmpty()) {
            MinioValidator.verificationDirectory(
                    parentPath,
                    () -> minioStorageService.getStatObject(parentPath),
                    () -> minioStorageService.listDirectoryItems(parentPath, false),
                    "Folder not found: " + parentPath);
        }

        MinioUtils.handleMinioException(() -> {
            minioStorageService.putEmptyItem(path);
            return null;
        }, "Failed to create directory: " + path);

        return ResourceResponseDtoFactory.createDtoFromPathComponents(pathComponents);
    }

    private boolean checkDirectoryExist(String path) {
        try {
            MinioUtils.handleMinioException(() -> minioStorageService.getStatObject(path),
                    "Failed to check directory existence: " + path);
            return true;
        } catch (ResourceNotFoundException e) {
            return MinioUtils.handleMinioException(
                    () -> minioStorageService.listDirectoryItems(path, false).iterator().hasNext(),
                    "Failed to check directory content: " + path);
        }

    }
}