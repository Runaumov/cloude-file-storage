package com.runaumov.spring.cloudfilestorage.service;

import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceDeleteService {
    private final MinioStorageService minioStorageService;

    public void deleteResource(String path) {
        try {
            minioStorageService.deleteItemForPath(path);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удаоении ресурса", e); // TODO
        }
    }
}
