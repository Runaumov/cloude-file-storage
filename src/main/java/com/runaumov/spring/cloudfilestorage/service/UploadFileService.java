package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDtoFactory;
import com.runaumov.spring.cloudfilestorage.exception.InvalidPathException;
import com.runaumov.spring.cloudfilestorage.exception.ResourceAlreadyExistsException;
import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
import com.runaumov.spring.cloudfilestorage.util.MinioValidator;
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

        if (!path.isEmpty()) {
            MinioValidator.verificationDirectory(
                    path,
                    () -> minioStorageService.getStatObject(path),
                    () -> minioStorageService.listDirectoryItems(path, false),
                    "Folder not found: " + path
            );
        }

        List<ResourceResponseDto> uploadFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            String objectOriginalName = file.getOriginalFilename();

            if (objectOriginalName == null || objectOriginalName.isEmpty()) {
                throw new InvalidPathException("File name cannot be empty");
            }

            String targetPath = path + objectOriginalName;

            if (checkObjectExists(targetPath)) {
                throw new ResourceAlreadyExistsException("File already exists: " + path);
            }

            ResourceResponseDto dto = uploadFile(file, targetPath);
            uploadFiles.add(dto);
        }
        return uploadFiles;
    }

    private ResourceResponseDto uploadFile(MultipartFile file, String path) {
        return MinioUtils.handleMinioException(() -> {
            minioStorageService.putObject(path, file);
            PathComponents pathComponents = pathParserService.parsePath(path);
            StatObjectResponse statObjectResponse = minioStorageService.getStatObject(path);
            return ResourceResponseDtoFactory.createDtoFromStatObject(statObjectResponse, pathComponents);
        }, "Failed to upload file: " + path);
    }

    private boolean checkObjectExists(String path) {
        return MinioUtils.handleMinioException(
                () -> minioStorageService.objectExist(path),
                "Failed to check object existence: " + path
        );
    }
}