package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDtoFactory;
import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UploadFileService {
    private final MinioStorageService minioStorageService;
    private final PathParserService pathParserService;

    public List<ResourceResponseDto> uploadFiles(String path, List<MultipartFile> files) {
        List<ResourceResponseDto> uploadFiles = new ArrayList<>();
        String normalizedPath = pathParserService.normalizePath(path);

        for (MultipartFile file : files) {
            String objectName = normalizedPath + file.getOriginalFilename();

            ResourceResponseDto dto= MinioUtils.handleMinioException(() -> {
                minioStorageService.putObject(objectName, file);
                PathComponents pathComponents = pathParserService.parsePath(objectName);
                StatObjectResponse statObject = minioStorageService.getStatObject(objectName);
                return ResourceResponseDtoFactory.createDtoFromStatObject(statObject, pathComponents);
            }, "Failed to upload resource");

            uploadFiles.add(dto);
        }
        return uploadFiles;
    }
}