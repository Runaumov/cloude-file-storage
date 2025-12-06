package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import io.minio.GetObjectArgs;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
        String userPrefix = "user-1-files/";
        String object1 = userPrefix + "directory1/";
        String object2 = userPrefix + "directory1/file2.txt";
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
                    .stream(inputStream2, content.length, -1)
                    .build());
        }

        List<ResourceResponseDto> list = directoryService.getDirectoryInfo("directory1/");

        Assertions.assertNotNull(list);
        Assertions.assertFalse(list.isEmpty());

        List<ResourceResponseDto> files = list.stream()
                .filter(dto -> "FILE".equals(dto.getType()))
                .toList();

        Assertions.assertEquals(1, files.size());

        ResourceResponseDto fileDto = files.get(0);
        Assertions.assertEquals("directory1/", fileDto.getPath());
        Assertions.assertEquals("file2.txt", fileDto.getName());
        Assertions.assertEquals("FILE", fileDto.getType());
    }

    @Test
    void shouldCreateEmptyDirectoryAndReturnCorrectDto_whenParentPathExists() throws Exception {
        String userPrefix = "user-1-files/";
        String parentDir = userPrefix + "directory/";

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{})) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(getTestBucketName())
                    .object(parentDir)
                    .stream(inputStream, 0, -1)
                    .build());
        }

        String path = "directory/subdirectory/";
        ResourceResponseDto dto = directoryService.createEmptyDirectory(path);

        String fullPath = userPrefix + path;
        try (InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(getTestBucketName())
                .object(fullPath)
                .build())) {
            byte[] content = inputStream.readAllBytes();
            Assertions.assertEquals(0, content.length);
        }

        Assertions.assertNotNull(dto);
        Assertions.assertEquals("directory/", dto.getPath());
        Assertions.assertEquals("subdirectory/", dto.getName());
        Assertions.assertNull(dto.getSize());
        Assertions.assertEquals("DIRECTORY", dto.getType());
    }
}