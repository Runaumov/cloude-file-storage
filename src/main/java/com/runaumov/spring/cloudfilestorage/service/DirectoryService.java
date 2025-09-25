package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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

    public ResourceResponseDto createEmptyDirectory(String path) {
        String dir = (path == null) ? "" : path.endsWith("/") ? path : path + "/";
        String trimmed = dir.endsWith("/") ? dir.substring(0, dir.length() - 1) : dir;
        int lastSlash = trimmed.lastIndexOf('/');

        String name;
        String parentPath;
        if (lastSlash == -1) {
            name = trimmed;
            parentPath = "";
        } else {
            name = trimmed.substring(lastSlash + 1);
            parentPath = trimmed.substring(0, lastSlash + 1);
        }

        byte[] empty = new byte[0];
        try (InputStream inputStream = new ByteArrayInputStream(empty)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(dir)
                    .stream(inputStream, 0, -1)
                    .contentType("application/x-directory")
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании жиректории" + e.getMessage());
        }

        return new ResourceResponseDto(parentPath, name, "DIRECTORY");

    }

}
