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
    private final AuthenticationService authenticationService;
    private final UserPathService userPathService;
    private final PathParserService pathParserService;

    public List<ResourceResponseDto> uploadFiles(String path, List<MultipartFile> files) {
        Long userId = authenticationService.getCurrentUserId();
        String userPath = userPathService.addUserPrefix(userId, path);

        if (!userPath.equals(userPathService.getUserPrefix(userId))) {
            MinioValidator.verificationDirectory(
                    userPath,
                    () -> minioStorageService.getStatObject(userPath),
                    () -> minioStorageService.listDirectoryItems(userPath, false),
                    "Folder not found: " + path
            );
        }

        List<ResourceResponseDto> uploadFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            String objectOriginalName = file.getOriginalFilename();

            if (objectOriginalName == null || objectOriginalName.isEmpty()) {
                throw new InvalidPathException("File name cannot be empty");
            }

            String targetUserPath = userPath + objectOriginalName;

            if (checkObjectExists(targetUserPath)) {
                throw new ResourceAlreadyExistsException("File already exists: " + path);
            }

            ResourceResponseDto dto = uploadFile(file, targetUserPath, userId);
            uploadFiles.add(dto);
        }
        return uploadFiles;
    }

    private ResourceResponseDto uploadFile(MultipartFile file, String path, Long userId) {
        return MinioUtils.handleMinioException(() -> {
            minioStorageService.putObject(path, file);

            String pathWithoutPrefix = userPathService.removeUserPrefix(userId, path);
            PathComponents pathComponents = pathParserService.parsePath(pathWithoutPrefix);

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