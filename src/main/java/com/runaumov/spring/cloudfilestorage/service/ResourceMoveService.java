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
                    minioStorageService.copyObject(to, from);
                    minioStorageService.deleteItemForPath(from);

                    StatObjectResponse statObject = minioStorageService.getStatObject(to);
                    PathComponents pathComponents = pathParserService.parsePath(to);

                    return ResourceResponseDtoFactory.createDtoFromStatObject(statObject, pathComponents);
                } else {
                    String normalizedFrom = pathParserService.normalizePath(from);
                    String normalizedTo = pathParserService.normalizePath(to);

                    var results = minioStorageService.listDirectoryItems(normalizedFrom, true);

                    for (Result<Item> result : results) {
                        Item item = result.get();
                        String oldKey = item.objectName();
                        String newKey = normalizedTo + oldKey.substring(normalizedFrom.length());

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
}
