package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceMoveService {

    private final MinioClient minioClient;
    @Value("${minio.bucket}")
    private String bucketName;

    public ResourceResponseDto resourceMove(String from, String to) {
        String name = to.contains("/")
                ? to.substring(to.lastIndexOf("/") + 1)
                : to;

        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                     .bucket(bucketName)
                     .object(to)
                     .source(CopySource.builder()
                             .bucket(bucketName)
                             .object(from)
                             .build())
                    .build());

            minioClient.removeObject(RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(from)
                    .build());

            StatObjectResponse statObject = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(to)
                    .build());

            int lastSlash = to.lastIndexOf('/');
            String parentPath = (lastSlash == -1) ? "" : to.substring(0, lastSlash + 1);

            ResourceResponseDto resourceResponseDto = new ResourceResponseDto();
            resourceResponseDto.setPath(parentPath);
            resourceResponseDto.setName(name);
            resourceResponseDto.setSize(statObject.size());
            resourceResponseDto.setType("FILE");

            return resourceResponseDto;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при перемещении файла", e);
        }
    }
}
