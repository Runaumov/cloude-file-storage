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
    private final AuthenticationService authenticationService;
    private final UserPathService userPathService;
    private final PathParserService pathParserService;

    public List<ResourceResponseDto> getDirectoryInfo(String path) {

        Long userId = authenticationService.getCurrentUserId();
        String userPath = userPathService.addUserPrefix(userId, path);

        if (!userPath.equals(userPathService.getUserPrefix(userId))) {
            MinioValidator.verificationDirectory(
                    userPath,
                    () -> minioStorageService.getStatObject(userPath),
                    () -> minioStorageService.listDirectoryItems(userPath, false),
                    "Folder not found: " + path
            );
        }

        List<ResourceResponseDto> resources = new ArrayList<>();
        var results = minioStorageService.listDirectoryItems(userPath, false);

        for (Result<Item> result : results) {
            Item item = MinioUtils.handleMinioException(result::get, "Failed to read directory item: " + path);
            String itemPath = userPathService.removeUserPrefix(userId, item.objectName());
            PathComponents itemPathComponents = pathParserService.parsePath(itemPath);
            ResourceResponseDto resourceResponseDto = ResourceResponseDtoFactory.createDtoFromItemAndPathComponents(item, itemPathComponents);
            resources.add(resourceResponseDto);
        }
        return resources;
    }

    public ResourceResponseDto createEmptyDirectory(String path) {

        Long userId = authenticationService.getCurrentUserId();
        String userPath = userPathService.addUserPrefix(userId, path);

        if (checkDirectoryExist(userPath)) {
            throw new ResourceAlreadyExistsException("Resource already exists: " + path);
        }

        PathComponents pathComponents = pathParserService.parsePath(userPath);
        String parentPath = pathComponents.path();

        if (!parentPath.isEmpty() && !parentPath.equals(userPathService.getUserPrefix(userId))) {
            MinioValidator.verificationDirectory(
                    parentPath,
                    () -> minioStorageService.getStatObject(parentPath),
                    () -> minioStorageService.listDirectoryItems(parentPath, false),
                    "Folder not found: " + userPathService.removeUserPrefix(userId, parentPath));
        }

        MinioUtils.handleMinioException(() -> {
            minioStorageService.putEmptyItem(userPath);
            return null;
        }, "Failed to create directory: " + path);

        String pathWithoutPrefix = userPathService.removeUserPrefix(userId, pathComponents.path());
        PathComponents userPathComponents = new PathComponents(pathWithoutPrefix, pathComponents.name());
        return ResourceResponseDtoFactory.createDtoFromPathComponents(userPathComponents);
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