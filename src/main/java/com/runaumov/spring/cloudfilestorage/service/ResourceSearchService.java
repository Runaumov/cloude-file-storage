package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceSearchService {

    private final MinioClient minioClient;
    @Value("${minio.bucket}")
    private String bucketName;

    public ResourceResponseDto searchResource(String query) {
        String name = query.contains("/")
                ? query.substring(query.lastIndexOf("/") + 1)
                : query;

        try {
            StatObjectResponse statObjectResponse = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(query)
                            .build()
            );
            ResourceResponseDto resourceResponseDto = new ResourceResponseDto();
            resourceResponseDto.setPath(query);
            resourceResponseDto.setName(name);
            resourceResponseDto.setSize(statObjectResponse.size());
            resourceResponseDto.setType("FILE");
            return resourceResponseDto;
        } catch (Exception e) {
            throw new RuntimeException("Файл не найден", e);
        }
    }
}
