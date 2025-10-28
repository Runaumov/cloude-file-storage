package com.runaumov.spring.cloudfilestorage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractServiceTest {

    protected static final String TEST_BUCKET = "test-bucket";
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
    }

    protected String getTestBucketName() {
        return TEST_BUCKET;
    }

}
