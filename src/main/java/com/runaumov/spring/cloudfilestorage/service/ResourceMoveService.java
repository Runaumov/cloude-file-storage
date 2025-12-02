package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDtoFactory;
import com.runaumov.spring.cloudfilestorage.exception.InvalidPathException;
import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
import com.runaumov.spring.cloudfilestorage.util.MinioValidator;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceMoveService {
    private final MinioStorageService minioStorageService;
    private final PathParserService pathParserService;

    public ResourceResponseDto resourceMove(String oldPath, String newPath) {

        checkOldNewPaths(oldPath, newPath);

        return MinioUtils.handleMinioException(() -> {
            if (!pathParserService.isDirectory(oldPath)) {
                MinioUtils.handleMinioException(() -> minioStorageService.getStatObject(oldPath),
                        "File not found: " + oldPath);

                String targetPath = resolveConflict(newPath);

                minioStorageService.copyObject(targetPath, oldPath);
                minioStorageService.deleteItemForPath(oldPath);

                StatObjectResponse statObject = minioStorageService.getStatObject(targetPath);
                PathComponents pathComponents = pathParserService.parsePath(targetPath);

                return ResourceResponseDtoFactory.createDtoFromStatObject(statObject, pathComponents);
            } else {
                MinioValidator.verificationDirectory(
                        oldPath,
                        () -> minioStorageService.getStatObject(oldPath),
                        () -> minioStorageService.listDirectoryItems(oldPath, false),
                        "Folder not found: " + oldPath);

                var results = minioStorageService.listDirectoryItems(oldPath, true);

                for (Result<Item> result : results) {
                    Item item = result.get();
                    String oldKey = item.objectName();
                    String newKeyBase = newPath + oldKey.substring(oldPath.length());
                    String newKey = resolveConflict(newKeyBase);

                    minioStorageService.copyObject(newKey, oldKey);
                    minioStorageService.deleteItemForPath(oldKey);
                }

                PathComponents pathComponents = pathParserService.parsePath(newPath);
                return ResourceResponseDtoFactory.createDtoFromPathComponents(pathComponents);
            }
        }, "Failed to move resources from: " + oldPath + " to: " + newPath);
    }

    private String resolveConflict(String path) {
        String resolvedPath = path;
        int copyIndex = 1;

        while (checkObjectExists(resolvedPath)) {
            resolvedPath = appendCopySuffix(path, copyIndex++);
        }
        return resolvedPath;
    }

    private String appendCopySuffix(String path, int copyIndex) {
        if (pathParserService.isDirectory(path)) {
            String pathWithoutSlash = path.substring(0, path.length() - 1);
            return pathWithoutSlash + "_copy" + copyIndex + '/';
        }

        int dotIndex = path.lastIndexOf('.');
        if (dotIndex > 0) {
            return path.substring(0, dotIndex) + "_copy" + copyIndex + path.substring(dotIndex);
        } else {
            return path + "_copy" + copyIndex;
        }
    }

    private boolean checkObjectExists(String path) {
        return MinioUtils.handleMinioException(
                () -> minioStorageService.objectExist(path),
                "Failed to check object existence: " + path
        );
    }

    private void checkOldNewPaths(String oldPath, String newPath) {
        if (pathParserService.isDirectory(oldPath)) {
            if (oldPath.equals(newPath)) {
                throw new InvalidPathException("Cannot move directory into itself: " + oldPath);
            }
            if (newPath.startsWith(oldPath)) {
                throw new InvalidPathException(
                        "Cannot move directory into its own subdirectory: " + newPath
                );
            }
        }
    }
}