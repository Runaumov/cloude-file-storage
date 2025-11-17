package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDtoFactory;
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
            try {
                String objectName = normalizedPath + file.getOriginalFilename();

                minioStorageService.putObject(objectName, file);
                PathComponents pathComponents = pathParserService.parsePath(objectName);
                StatObjectResponse statObject = minioStorageService.getStatObject(objectName);

                ResourceResponseDto dto = ResourceResponseDtoFactory.createDtoFromStatObject(statObject, pathComponents);
                uploadFiles.add(dto);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при загрузке файла");
            }
        }
        return uploadFiles;
    }
}