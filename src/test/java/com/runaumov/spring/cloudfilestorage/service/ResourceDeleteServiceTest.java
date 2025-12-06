package com.runaumov.spring.cloudfilestorage.service;

import io.minio.ListObjectsArgs;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test-service")
public class ResourceDeleteServiceTest extends AbstractServiceTest {

    private final ResourceDeleteService resourceDeleteService;

    @Autowired
    public ResourceDeleteServiceTest(ResourceDeleteService resourceDeleteService) {
        this.resourceDeleteService = resourceDeleteService;
    }

    @Test
    void shouldDeleteFile_whenFileExist() throws Exception {
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

        resourceDeleteService.deleteResource("file.txt");

        boolean fileExists = false;
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(getTestBucketName())
                        .prefix(userPrefix)
                        .recursive(false)
                        .build()
        );

        for (Result<Item> result : results) {
            Item item = result.get();
            if (item.objectName().equals(objectPath)) {
                fileExists = true;
                break;
            }
        }
        Assertions.assertFalse(fileExists);
    }
}
