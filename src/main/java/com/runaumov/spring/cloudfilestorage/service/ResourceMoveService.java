package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDtoFactory;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceMoveService {
    private final MinioStorageService minioStorageService;
    private final PathParserService pathParserService;

    public ResourceResponseDto resourceMove(String from, String to) {
        try {
            minioStorageService.copyObject(to, from);

            minioStorageService.deleteItemForPath(from);

            StatObjectResponse statObject1 = minioStorageService.getStatObject(to);

            PathComponents pathComponents = pathParserService.parsePath(to);

            return ResourceResponseDtoFactory.createDtoFromStatObject(statObject1, pathComponents);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при перемещении файла", e);
        }
    }
}
