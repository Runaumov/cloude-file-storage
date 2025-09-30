package com.runaumov.spring.cloudfilestorage.service;

import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class ResourceDownloadService {

    private final MinioClient minioClient;
    @Value("${minio.bucket}")
    private String bucketName;

    public byte[] resourceDownload(String path) {
        try {
            if (path.endsWith("/")) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
                    Iterable<Result<Item>> results = minioClient.listObjects(
                            ListObjectsArgs.builder()
                                    .bucket(bucketName)
                                    .prefix(path)
                                    .recursive(true)
                                    .build()
                    );

                    for (Result<Item> result : results) {
                        Item item = result.get();
                        String objectName = item.objectName();

                        try (InputStream inputStream = minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(objectName)
                                        .build()
                        )) {
                            ZipEntry zipEntry = new ZipEntry(objectName.substring(path.length()));
                            zipOutputStream.putNextEntry(zipEntry);
                            inputStream.transferTo(zipOutputStream);
                            zipOutputStream.closeEntry();
                        }
                    }
                } return byteArrayOutputStream.toByteArray();
            } else {
                try (InputStream inputStream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucketName)
                                .object(path)
                                .build()
                )) {
                    return inputStream.readAllBytes();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при скачивании ресурса", e);
        }
    }
}
