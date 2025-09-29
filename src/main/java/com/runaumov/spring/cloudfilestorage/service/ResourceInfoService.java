package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceInfoService {

    private final MinioClient minioClient;
    @Value("${minio.bucket}")
    private String bucketName;

        public ResourceResponseDto getResourceInfo(String path) {
            String name = path.contains("/")
                    ? path.substring(path.lastIndexOf("/") + 1)
                    : path;

            String parentPath = path.contains("/")
                    ? path.substring(0, path.lastIndexOf("/") + 1)
                    : ""; // TODO : сделать утитлитный метод

            try {
                StatObjectResponse minioStatObject = minioClient.statObject(StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(path)
                        .build());

                ResourceResponseDto resourceResponseDto = new ResourceResponseDto();
                resourceResponseDto.setPath(parentPath);
                resourceResponseDto.setName(name);
                resourceResponseDto.setSize(minioStatObject.size());
                resourceResponseDto.setType("FILE");

                return resourceResponseDto;
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при получении информации о директории", e);
            }
        }
}
