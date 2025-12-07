package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.service.user.UserContextService;
import io.minio.*;
import io.minio.messages.Item;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Testcontainers
public abstract class AbstractServiceTest {

    protected static final String TEST_BUCKET = "test-bucket";

    @MockitoBean
    protected UserContextService userContextService;

    @Autowired protected MinioClient minioClient;

    @Container
    static final MinIOContainer MINIO_CONTAINER = new MinIOContainer("minio/minio")
            .withUserName("miniologin")
            .withPassword("miniopassword");

    @DynamicPropertySource
    static void registerMinioProperties(DynamicPropertyRegistry registry) {
        registry.add("minio.url", MINIO_CONTAINER::getS3URL);
        registry.add("minio.access-key", MINIO_CONTAINER::getUserName);
        registry.add("minio.secret-key", MINIO_CONTAINER::getPassword);
        registry.add("minio.bucket", () -> TEST_BUCKET);
    }

    @BeforeEach
    void setup() throws Exception {
        boolean isBucketExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(TEST_BUCKET).build());
        if (!isBucketExist) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(TEST_BUCKET).build());
        }
        setupUserContextMocks();
    }

    protected void setupUserContextMocks() {
        when(userContextService.addUserPrefix(anyString())).thenAnswer(invocation -> {
            String path = invocation.getArgument(0);
            return "user-1-files/" + path;
        });

        when(userContextService.removeUserPrefix(anyString())).thenAnswer(invocation -> {
            String path = invocation.getArgument(0);
            if (path.startsWith("user-1-files/")) {
                return path.substring("user-1-files/".length());
            }
            return path;
        });

        when(userContextService.getUserPrefix()).thenReturn("user-1-files/");

        when(userContextService.isUserRoot(anyString())).thenAnswer(invocation -> {
            String path = invocation.getArgument(0);
            return "user-1-files/".equals(path);
        });
    }

    protected String getTestBucketName() {
        return TEST_BUCKET;
    }

}
