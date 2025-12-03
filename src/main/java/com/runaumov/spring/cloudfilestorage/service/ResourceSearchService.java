package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDtoFactory;
import com.runaumov.spring.cloudfilestorage.exception.ResourceNotFoundException;
import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
import com.runaumov.spring.cloudfilestorage.util.MinioValidator;
import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ResourceSearchService {
    private final MinioStorageService minioStorageService;
    private final PathParserService pathParserService;

    public List<ResourceResponseDto> searchResource(String query) {

        return MinioUtils.handleMinioException(() -> {
            Iterable<Result<Item>> results = minioStorageService.listDirectoryItems("", true);
            List<ResourceResponseDto> foundResources = new ArrayList<>();

            for (Result<Item> result : results) {
                Item item = result.get();
                String foundPath = item.objectName();
                PathComponents pathComponents = pathParserService.parsePath(foundPath);

                if (matchesQuery(pathComponents.name(), query)) {
                    ResourceResponseDto dto = ResourceResponseDtoFactory.createDtoFromItemAndPathComponents(item, pathComponents);
                    foundResources.add(dto);
                }
            }
            return foundResources;
        }, "Failed to search resource: " + query);
    }

    private boolean matchesQuery(String name, String query) {
        return name.toLowerCase().contains(query.toLowerCase());
    }
}