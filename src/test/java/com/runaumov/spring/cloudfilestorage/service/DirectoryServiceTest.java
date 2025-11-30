package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.AbstractServiceTest;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDirectoryDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test-service")
public class DirectoryServiceTest extends AbstractServiceTest {

    private final DirectoryService directoryService;

    @Autowired
    public DirectoryServiceTest(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @Test
    void shouldGetListResourceResponseDto_whenDirectoryExist() throws Exception {
        String object1 = "directory1/";
        String object2 = "file1.txt";
        String object3 = "directory1/file2.txt";
        byte[] content = "test".getBytes();

        try (ByteArrayInputStream inputStream1 = new ByteArrayInputStream(new byte[]{})) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(getTestBucketName())
                    .object(object1)
                    .stream(inputStream1, 0, -1)
                    .build());
        }

        try (ByteArrayInputStream inputStream2 = new ByteArrayInputStream(content)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(getTestBucketName())
                    .object(object2)
                    .stream(inputStream2, 0, -1)
                    .build());
        }

        try (ByteArrayInputStream inputStream3 = new ByteArrayInputStream(content)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(getTestBucketName())
                    .object(object3)
                    .stream(inputStream3, 0, -1)
                    .build());
        }

        List<ResourceResponseDto> list = directoryService.getDirectoryInfo("directory1");

        Assertions.assertNotNull(list);
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertTrue(list.get(0) instanceof ResourceResponseDto);
    }

    @Test
    void shouldCreateEmptyDirectoryAndReturnCorrectDto_whenPathExist() throws Exception {
        String path = "directory/subdirectory/";

        ResourceResponseDto dto = directoryService.createEmptyDirectory(path);

        try (InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(getTestBucketName())
                .object(path)
                .build())) {
            byte[] content = inputStream.readAllBytes();
            Assertions.assertEquals(0, content.length);
        }

        Assertions.assertNotNull(dto);
        Assertions.assertEquals("directory/subdirectory/", dto.getPath());
        Assertions.assertEquals("", dto.getName());
        Assertions.assertNull(dto.getSize());
        Assertions.assertEquals("DIRECTORY", dto.getType());
    }

}
