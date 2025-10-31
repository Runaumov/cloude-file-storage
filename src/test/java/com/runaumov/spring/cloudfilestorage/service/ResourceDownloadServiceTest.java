package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.AbstractServiceTest;
import io.minio.ListObjectsArgs;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@SpringBootTest
public class ResourceDownloadServiceTest extends AbstractServiceTest {

    private final ResourceDownloadService resourceDownloadService;

    @Autowired
    public ResourceDownloadServiceTest(ResourceDownloadService resourceDownloadService) {
        this.resourceDownloadService = resourceDownloadService;
    }


    @Test
    void shouldDownloadFile_whenFileExist() throws Exception {
        String objectPath = "file.txt";
        byte[] content = "test".getBytes(StandardCharsets.UTF_8);
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(getTestBucketName())
                    .object(objectPath)
                    .stream(byteArrayInputStream, content.length, -1)
                    .contentType("text/plain")
                    .build());
        }

        byte[] resourceDownload = resourceDownloadService.resourceDownload(objectPath);

        Assertions.assertNotNull(resourceDownload);
        Assertions.assertArrayEquals(content, resourceDownload);
    }
}
