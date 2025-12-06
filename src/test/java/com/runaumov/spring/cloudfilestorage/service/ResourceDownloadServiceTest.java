package com.runaumov.spring.cloudfilestorage.service;

import io.minio.PutObjectArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@SpringBootTest
@ActiveProfiles("test-service")
public class ResourceDownloadServiceTest extends AbstractServiceTest {

    private final ResourceDownloadService resourceDownloadService;

    @Autowired
    public ResourceDownloadServiceTest(ResourceDownloadService resourceDownloadService) {
        this.resourceDownloadService = resourceDownloadService;
    }

    @Test
    void shouldDownloadFile_whenFileExist() throws Exception {
        String userPrefix = "user-1-files/";
        String objectPath = userPrefix + "file.txt";
        byte[] content = "test".getBytes(StandardCharsets.UTF_8);

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(getTestBucketName())
                    .object(objectPath)
                    .stream(byteArrayInputStream, content.length, -1)
                    .contentType("text/plain")
                    .build());
        }

        byte[] resourceDownload = resourceDownloadService.resourceDownload("file.txt");

        Assertions.assertNotNull(resourceDownload);
        Assertions.assertArrayEquals(content, resourceDownload);
    }
}
