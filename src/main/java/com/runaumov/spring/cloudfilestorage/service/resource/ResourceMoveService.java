package com.runaumov.spring.cloudfilestorage.service.resource;

import com.runaumov.spring.cloudfilestorage.dto.path.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.resource.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.resource.ResourceResponseDtoFactory;
import com.runaumov.spring.cloudfilestorage.exception.InvalidPathException;
import com.runaumov.spring.cloudfilestorage.service.storage.MinioStorageService;
import com.runaumov.spring.cloudfilestorage.service.storage.PathParserService;
import com.runaumov.spring.cloudfilestorage.service.user.UserContextService;
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
    private final UserContextService userContextService;

    public ResourceResponseDto resourceMove(String oldPath, String newPath) {
        String oldUserPath = userContextService.addUserPrefix(oldPath);
        String newUserPath = userContextService.addUserPrefix(newPath);

        checkOldNewPaths(oldUserPath, newUserPath, oldPath, newPath);

        return MinioUtils.handleMinioException(() -> {
            if (!pathParserService.isDirectory(oldUserPath)) {

                MinioUtils.handleMinioException(() -> minioStorageService.getStatObject(oldUserPath),
                        "File not found: " + oldPath);

                String targetUserPath = resolveConflict(newUserPath);

                minioStorageService.copyObject(targetUserPath, oldUserPath);
                minioStorageService.deleteItemForPath(oldUserPath);

                StatObjectResponse statObject = minioStorageService.getStatObject(targetUserPath);
                String targetPathWithoutPrefix = userContextService.removeUserPrefix(targetUserPath);
                PathComponents pathComponents = pathParserService.parsePath(targetPathWithoutPrefix);

                return ResourceResponseDtoFactory.createDtoFromStatObject(statObject, pathComponents);
            } else {
                MinioValidator.verificationDirectory(
                        oldUserPath,
                        () -> minioStorageService.getStatObject(oldUserPath),
                        () -> minioStorageService.listDirectoryItems(oldUserPath, false),
                        "Folder not found: " + oldPath);

                String targetBasePath = resolveConflict(newUserPath);
                var results = minioStorageService.listDirectoryItems(oldUserPath, true);

                for (Result<Item> result : results) {
                    Item item = result.get();
                    String oldKey = item.objectName();
                    String relativePath = oldKey.substring(oldUserPath.length());
                    String newKey = targetBasePath + relativePath;

                    minioStorageService.copyObject(newKey, oldKey);
                    minioStorageService.deleteItemForPath(oldKey);
                }

                try {
                    minioStorageService.deleteItemForPath(oldUserPath);
                } catch (Exception ignored) {
                }

                String targetPathWithoutPrefix = userContextService.removeUserPrefix(targetBasePath);
                PathComponents pathComponents = pathParserService.parsePath(targetPathWithoutPrefix);

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

    private void checkOldNewPaths(String oldUserPath, String newUserPath, String oldPath, String newPath) {
        if (oldUserPath.equals(newUserPath)) {
            throw new InvalidPathException("Source and destination paths are the same: " + oldPath);
        }
        if (pathParserService.isDirectory(oldUserPath)) {
            if (newUserPath.startsWith(oldUserPath)) {
                throw new InvalidPathException(
                        "Cannot move directory into its own subdirectory: " + newPath
                );
            }

            if (!pathParserService.isDirectory(newUserPath)) {
                throw new InvalidPathException(
                        "Cannot move directory to a file path: " + newPath
                );
            }
        } else {
            if (pathParserService.isDirectory(newUserPath)) {
                throw new InvalidPathException(
                        "Cannot move file to a directory path: " + newPath
                );
            }
        }
    }
}