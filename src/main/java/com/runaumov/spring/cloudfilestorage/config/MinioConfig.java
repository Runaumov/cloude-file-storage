package com.runaumov.spring.cloudfilestorage.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${minio.url}")
    private String url;
    @Value("${minio.login}")
    private String login;
    @Value("${minio.password}")
    private String password;
    @Value("${minio.bucket}")
    private String bucket;

    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(login, password)
                .build();
    }

}
