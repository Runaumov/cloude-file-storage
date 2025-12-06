package com.runaumov.spring.cloudfilestorage.dto;

import com.runaumov.spring.cloudfilestorage.model.ResourceType;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;

public class ResourceResponseDtoFactory {

    public static ResourceResponseDto createDtoFromItemAndPathComponents(Item item, PathComponents pathComponents) {
        boolean isDirectory = item.isDir() || pathComponents.name().endsWith("/");
        ResourceType resourceType = isDirectory ? ResourceType.DIRECTORY : ResourceType.FILE;

        return createDto(pathComponents, resourceType, item.size());
    }

    public static ResourceResponseDto createDtoFromPathComponents(PathComponents pathComponents) {
        return createDto(pathComponents, ResourceType.DIRECTORY, null);
    }

    public static ResourceResponseDto createDtoFromStatObject(StatObjectResponse statObject, PathComponents pathComponents) {
        return createDto(pathComponents, ResourceType.FILE, statObject.size());
    }

    private static ResourceResponseDto createDto(PathComponents pathComponents, ResourceType resourceType, Long size) {
        ResourceResponseDto.ResourceResponseDtoBuilder builder = ResourceResponseDto.builder()
                .path(pathComponents.path())
                .name(pathComponents.name())
                .type(resourceType.name());
        if (resourceType == ResourceType.FILE && size != null) {
            builder.size(size);
        }
        return builder.build();
    }
}
