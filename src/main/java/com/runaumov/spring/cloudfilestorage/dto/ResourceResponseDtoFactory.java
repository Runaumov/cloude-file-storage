package com.runaumov.spring.cloudfilestorage.dto;

import com.runaumov.spring.cloudfilestorage.model.ResourceType;
import io.minio.messages.Item;

public class ResourceResponseDtoFactory {

    public static ResourceResponseDto createDtoFromItemAndPathComponents(Item item, PathComponents pathComponents) {
        String itemType = item.isDir() ? ResourceType.DIRECTORY.name() : ResourceType.FILE.name();

        return ResourceResponseDto.builder()
                .path(pathComponents.path())
                .name(pathComponents.name())
                .size(item.size())
                .type(itemType)
                .build();
    }

    public static ResourceResponseDto createDtoFromPathComponents(PathComponents pathComponents) {
        return ResourceResponseDto.builder()
                .path(pathComponents.path())
                .name(pathComponents.name())
                .type(ResourceType.DIRECTORY.name())
                .build();
    }
}
