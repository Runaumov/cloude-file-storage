package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectoryService {

    private final MinioClient minioClient;
    @Value("${minio.bucket}")
    private String bucketName;

    public List<ResourceResponseDto> getDirectoryInfo(String path) {
        List<ResourceResponseDto> resources = new ArrayList<>();

        String prefix = path.endsWith("/") ? path : path + "/";

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .recursive(false)
                    .build());

            for (Result<Item> result : results) {
                Item item = result.get();

                ResourceResponseDto resourceResponseDto = new ResourceResponseDto();

                String objectName = item.objectName();
                int lastSlashIndex = objectName.lastIndexOf('/');

                resourceResponseDto.setPath(objectName.substring(0, lastSlashIndex + 1));
                resourceResponseDto.setName(objectName.substring(lastSlashIndex + 1));

                if (item.isDir()) {
                    resourceResponseDto.setType("DIRECTORY");
                } else {
                    resourceResponseDto.setType("FILE");
                    resourceResponseDto.setSize(item.size());
                }

                resources.add(resourceResponseDto);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении информации о директории", e);
        }

        return resources;
    }

}
