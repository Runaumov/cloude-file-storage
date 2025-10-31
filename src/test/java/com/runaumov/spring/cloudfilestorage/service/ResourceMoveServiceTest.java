package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.AbstractServiceTest;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@SpringBootTest
public class ResourceMoveServiceTest extends AbstractServiceTest {

    private final ResourceMoveService resourceMoveService;

    @Autowired
    public ResourceMoveServiceTest(ResourceMoveService resourceMoveService) {
        this.resourceMoveService = resourceMoveService;
    }

    @Test
    void shouldMoveFile_whenFileExist() throws Exception {
        String objectPathFrom = "dir1/file.txt";
        String objectPathTo = "dir2/file.txt";
        byte[] content = "test".getBytes(StandardCharsets.UTF_8);

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(getTestBucketName())
                    .object(objectPathFrom)
                    .stream(byteArrayInputStream, content.length, -1)
                    .build());
        }

        ResourceResponseDto dto = resourceMoveService.resourceMove(objectPathFrom, objectPathTo);

        Assertions.assertEquals("dir2/", dto.getPath());
        Assertions.assertEquals("file.txt", dto.getName());
        Assertions.assertEquals(content.length, dto.getSize());
    }
}
