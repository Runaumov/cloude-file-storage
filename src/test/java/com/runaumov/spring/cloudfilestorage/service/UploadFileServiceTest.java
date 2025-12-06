package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test-service")
public class UploadFileServiceTest extends AbstractServiceTest {

    private final UploadFileService uploadFileService;

    @Autowired
    public UploadFileServiceTest(UploadFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }

    @Test
    void shouldGetResponseDto_whenFileUpload() throws Exception {
        String userPrefix = "user-1-files/";
        String objectPath = "dir/";

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{})) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(getTestBucketName())
                    .object(userPrefix + objectPath)
                    .stream(inputStream, 0, -1)
                    .build());
        }

        MockMultipartFile file1 = new MockMultipartFile(
                "file1", "test1.txt", "text/plain", "test1".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile file2 = new MockMultipartFile(
                "file2", "test2.txt", "text/plain", "test2".getBytes(StandardCharsets.UTF_8));

        List<MultipartFile> files = List.of(file1, file2);

        List<ResourceResponseDto> result = uploadFileService.uploadFiles(objectPath, files);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("test1.txt", result.get(0).getName());
        Assertions.assertEquals("test2.txt", result.get(1).getName());

        for (ResourceResponseDto dto : result) {
            String fullPath = userPrefix + dto.getPath() + dto.getName();
            StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(getTestBucketName())
                    .object(fullPath)
                    .build());
            Assertions.assertNotNull(statObjectResponse);
            Assertions.assertTrue(statObjectResponse.size() > 0);
        }
    }
}
