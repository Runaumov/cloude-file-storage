package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDtoFactory;
import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceMoveService {
    private final MinioStorageService minioStorageService;
    private final PathParserService pathParserService;

    public ResourceResponseDto resourceMove(String from, String to) {

        return MinioUtils.handleMinioException(() -> {
            if (!isDirectory(from)) {
                String targetPath = resolveConflict(to);

                minioStorageService.copyObject(targetPath, from);
                minioStorageService.deleteItemForPath(from);

                StatObjectResponse statObject = minioStorageService.getStatObject(targetPath);
                PathComponents pathComponents = pathParserService.parsePath(targetPath);

                return ResourceResponseDtoFactory.createDtoFromStatObject(statObject, pathComponents);
            } else {
                String normalizedFrom = pathParserService.normalizePath(from);
                String normalizedTo = pathParserService.normalizePath(to);

                var results = minioStorageService.listDirectoryItems(normalizedFrom, true);

                for (Result<Item> result : results) {
                    Item item = result.get();
                    String oldKey = item.objectName();
                    String newKeyBase = normalizedTo + oldKey.substring(normalizedFrom.length());
                    String newKey = resolveConflict(newKeyBase);

                    minioStorageService.copyObject(newKey, oldKey);
                    minioStorageService.deleteItemForPath(oldKey);
                }

                PathComponents pathComponents = pathParserService.parsePath(to);
                return ResourceResponseDtoFactory.createDtoFromPathComponents(pathComponents);
            }
        }, "Failed to move resources from: " + from + " to: " + to);
    }

    private boolean isDirectory(String path) {
        try {
            return minioStorageService.listDirectoryItems(pathParserService.normalizePath(path), false).iterator().hasNext();
        } catch (Exception e) {
            return false;
        }
    }

    private String resolveConflict(String path) {
        String pathWithSuffix = path;
        int copyIndex = 1;

        while (minioStorageService.exists(pathWithSuffix)) {
            pathWithSuffix = appendCopySuffix(path, copyIndex);
            copyIndex++;
        }
        return pathWithSuffix;
    }

    private String appendCopySuffix(String path, int copyIndex) {
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex > 0) {
            return path.substring(0, dotIndex) + "_copy" + copyIndex + path.substring(dotIndex);
        } else {
            return path + "_copy" + copyIndex;
        }
    }
}