package com.runaumov.spring.cloudfilestorage.service.resource;

import com.runaumov.spring.cloudfilestorage.dto.path.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.resource.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.resource.ResourceResponseDtoFactory;
import com.runaumov.spring.cloudfilestorage.exception.ResourceAlreadyExistsException;
import com.runaumov.spring.cloudfilestorage.exception.ResourceNotFoundException;
import com.runaumov.spring.cloudfilestorage.service.auth.storage.MinioStorageService;
import com.runaumov.spring.cloudfilestorage.service.auth.storage.PathParserService;
import com.runaumov.spring.cloudfilestorage.service.user.UserContextService;
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
    private final UserContextService userContextService;

    public List<ResourceResponseDto> getDirectoryInfo(String path) {
        String userPath = userContextService.addUserPrefix(path);

        if (!userContextService.isUserRoot(userPath)) {
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
            String itemPath = userContextService.removeUserPrefix(item.objectName());

            if (item.objectName().equals(userPath)) {
                continue;
            }

            PathComponents itemPathComponents = pathParserService.parsePath(itemPath);
            ResourceResponseDto resourceResponseDto = ResourceResponseDtoFactory.createDtoFromItemAndPathComponents(item, itemPathComponents);
            resources.add(resourceResponseDto);
        }
        return resources;
    }

    public ResourceResponseDto createEmptyDirectory(String path) {

        String userPath = userContextService.addUserPrefix(path);

        if (checkDirectoryExist(userPath)) {
            throw new ResourceAlreadyExistsException("Resource already exists: " + path);
        }

        PathComponents pathComponents = pathParserService.parsePath(userPath);
        String parentPath = pathComponents.path();

        if (!parentPath.isEmpty() && !userContextService.isUserRoot(parentPath)) {
            MinioValidator.verificationDirectory(
                    parentPath,
                    () -> minioStorageService.getStatObject(parentPath),
                    () -> minioStorageService.listDirectoryItems(parentPath, false),
                    "Folder not found: " + userContextService.removeUserPrefix(parentPath));
        }

        MinioUtils.handleMinioException(() -> {
            minioStorageService.putEmptyItem(userPath);
            return null;
        }, "Failed to create directory: " + path);

        String pathWithoutPrefix = userContextService.removeUserPrefix(pathComponents.path());
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