package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.resource.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.service.resource.ResourceInfoService;
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
public class ResourceInfoServiceTest extends AbstractServiceTest {

    private final ResourceInfoService resourceInfoService;

    @Autowired
    public ResourceInfoServiceTest(ResourceInfoService resourceInfoService) {
        this.resourceInfoService = resourceInfoService;
    }

    @Test
    void shouldGetResourceResponseDto_whenFileExist() throws Exception {
        String userPrefix = "user-1-files/";
        String objectPath = userPrefix + "file.txt";
        byte[] content = "test".getBytes(StandardCharsets.UTF_8);

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(getTestBucketName())
                            .object(objectPath)
                            .stream(byteArrayInputStream, content.length, -1)
                            .contentType("text/plain")
                            .build()
            );
        }

        ResourceResponseDto dto = resourceInfoService.getResourceInfo("file.txt");

        Assertions.assertEquals("", dto.getPath());
        Assertions.assertEquals("file.txt", dto.getName());
        Assertions.assertEquals(content.length, dto.getSize());
        Assertions.assertEquals("FILE", dto.getType());
    }
}
