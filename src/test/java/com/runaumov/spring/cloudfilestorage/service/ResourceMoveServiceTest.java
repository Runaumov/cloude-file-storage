package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.resource.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.service.resource.ResourceMoveService;
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
public class ResourceMoveServiceTest extends AbstractServiceTest {

    private final ResourceMoveService resourceMoveService;

    @Autowired
    public ResourceMoveServiceTest(ResourceMoveService resourceMoveService) {
        this.resourceMoveService = resourceMoveService;
    }

    @Test
    void shouldMoveFile_whenFileExist() throws Exception {
        String userPrefix = "user-1-files/";
        String objectPathFrom = userPrefix + "dir1/file.txt";
        byte[] content = "test".getBytes(StandardCharsets.UTF_8);

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(getTestBucketName())
                    .object(objectPathFrom)
                    .stream(byteArrayInputStream, content.length, -1)
                    .build());
        }

        ResourceResponseDto dto = resourceMoveService.resourceMove("dir1/file.txt", "dir2/file.txt");

        Assertions.assertEquals("dir2/", dto.getPath());
        Assertions.assertEquals("file.txt", dto.getName());
        Assertions.assertEquals(content.length, dto.getSize());
    }
}
