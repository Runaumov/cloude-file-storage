package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.AbstractServiceTest;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
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
public class ResourceSearchServiceTest extends AbstractServiceTest {

    private final ResourceSearchService resourceSearchService;

    @Autowired
    public ResourceSearchServiceTest(ResourceSearchService resourceSearchService) {
        this.resourceSearchService = resourceSearchService;
    }

    @Test
    void shouldSearchFile_whenFileExist() throws Exception {
        String objectPath = "directiry/file.txt";
        byte[] content = "test".getBytes(StandardCharsets.UTF_8);

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(getTestBucketName())
                    .object(objectPath)
                    .stream(byteArrayInputStream, content.length, -1)
                    .build());
        }

        ResourceResponseDto dto = resourceSearchService.searchResource(objectPath);

        Assertions.assertEquals("file.txt", dto.getName());
        Assertions.assertEquals(content.length, dto.getSize());
    }
}
