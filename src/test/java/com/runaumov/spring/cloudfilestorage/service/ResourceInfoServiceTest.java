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
public class ResourceInfoServiceTest extends AbstractServiceTest {

    private final ResourceInfoService resourceInfoService;

    @Autowired
    public ResourceInfoServiceTest(ResourceInfoService resourceInfoService) {
        this.resourceInfoService = resourceInfoService;
    }

    @Test
    void getResourceInfo_returnCorrectDto_forFileInRoot() throws Exception {
        String objectPath = "file-in-root.txt";
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

        ResourceResponseDto dto = resourceInfoService.getResourceInfo(objectPath);

        Assertions.assertEquals("", dto.getPath());
        Assertions.assertEquals("file-in-root.txt", dto.getName());
        Assertions.assertEquals(content.length, dto.getSize());
        Assertions.assertEquals("FILE", dto.getType());
    }
}
