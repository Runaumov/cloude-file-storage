package com.runaumov.spring.cloudfilestorage.service.resource;

import com.runaumov.spring.cloudfilestorage.dto.path.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.resource.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.resource.ResourceResponseDtoFactory;
import com.runaumov.spring.cloudfilestorage.service.storage.MinioStorageService;
import com.runaumov.spring.cloudfilestorage.service.storage.PathParserService;
import com.runaumov.spring.cloudfilestorage.service.user.UserContextService;
import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
import io.minio.Result;
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
    private final UserContextService userContextService;

    public List<ResourceResponseDto> searchResource(String query) {

        String userPrefix = userContextService.getUserPrefix();

        return MinioUtils.handleMinioException(() -> {
            Iterable<Result<Item>> results = minioStorageService.listDirectoryItems(userPrefix, true);
            List<ResourceResponseDto> foundResources = new ArrayList<>();

            for (Result<Item> result : results) {
                Item item = result.get();
                String foundPath = item.objectName();

                String pathWithoutPrefix = userContextService.removeUserPrefix(foundPath);
                PathComponents pathComponents = pathParserService.parsePath(pathWithoutPrefix);

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