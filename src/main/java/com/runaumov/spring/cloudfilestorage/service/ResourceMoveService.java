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
        String name = from.contains("/")
                ? from.substring(from.lastIndexOf("/") + 1)
                : from;

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

            ResourceResponseDto resourceResponseDto = new ResourceResponseDto();
            resourceResponseDto.setPath(from);
            resourceResponseDto.setName(name);
            resourceResponseDto.setSize(statObject.size());
            resourceResponseDto.setType("FILE");

            return resourceResponseDto;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при перемещении файла", e);
        }
    }
}
