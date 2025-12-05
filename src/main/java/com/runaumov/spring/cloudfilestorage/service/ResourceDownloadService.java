package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
import com.runaumov.spring.cloudfilestorage.util.MinioValidator;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class ResourceDownloadService {

    private final MinioStorageService minioStorageService;
    private final AuthenticationService authenticationService;
    private final UserPathService userPathService;
    private final PathParserService pathParserService;

    public byte[] resourceDownload(String path) {

        Long userId = authenticationService.getCurrentUserId();
        String userPath = userPathService.addUserPrefix(userId, path);

        if (pathParserService.isDirectory(userPath)) {
            if (!userPath.equals(userPathService.getUserPrefix(userId))) {
                MinioValidator.verificationDirectory(
                        userPath,
                        () -> minioStorageService.getStatObject(userPath),
                        () -> minioStorageService.listDirectoryItems(userPath, false),
                        "Folder not found: " + path);
            }
            return downloadDirectory(userPath, userId);
        } else {
            MinioUtils.handleMinioException(() -> minioStorageService.getStatObject(userPath), "File not found: " + path);
            return downloadFile(userPath);
        }
    }

    private byte[] downloadFile(String path) {
        return MinioUtils.handleMinioException(() -> {
            try (InputStream inputStream = minioStorageService.getObjectStream(path)) {
                return inputStream.readAllBytes();
            }
        }, "Failed to download file: " + path);
    }

    private byte[] downloadDirectory(String path, Long userId) {
        return MinioUtils.handleMinioException(() -> {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try (ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {
                if (hasItems(path)) {
                    addItemsToZip(path, zipOut, userId);
                } else {
                    addEmptyDirectoryMarker(zipOut);
                }
            }

            return byteArrayOutputStream.toByteArray();
        }, "Failed to download directory: " + path);
    }

    private boolean hasItems(String path) throws Exception {
        return minioStorageService.listDirectoryItems(path, false).iterator().hasNext();
    }

    private void addItemsToZip(String path, ZipOutputStream zipOut, Long userId) throws Exception {
        Iterable<Result<Item>> items = minioStorageService.listDirectoryItems(path, true);

        for (Result<Item> result : items) {
            Item item = result.get();
            String objectName = item.objectName();

            String pathWithoutPrefix = userPathService.removeUserPrefix(userId, objectName);
            String relativePath = calculateRelativePath(userPathService.removeUserPrefix(userId, path), pathWithoutPrefix);

            if (pathParserService.isDirectory(objectName)) {
                addDirectoryToZip(relativePath, zipOut);
            } else {
                addFileToZip(objectName, relativePath, zipOut);
            }
        }
    }

    private String calculateRelativePath(String basePath, String objectName) {
        String relativePath = objectName.substring(basePath.length());
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        return relativePath;
    }

    private void addDirectoryToZip(String relativePath, ZipOutputStream zipOut) throws Exception {
        ZipEntry entry = new ZipEntry(relativePath);
        zipOut.putNextEntry(entry);
        zipOut.closeEntry();
    }

    private void addFileToZip(String objectName, String relativePath, ZipOutputStream zipOut) throws Exception {
        try (InputStream inputStream = minioStorageService.getObjectStream(objectName)) {
            ZipEntry entry = new ZipEntry(relativePath);
            zipOut.putNextEntry(entry);
            inputStream.transferTo(zipOut);
            zipOut.closeEntry();
        }
    }

    private void addEmptyDirectoryMarker(ZipOutputStream zipOut) throws Exception {
        ZipEntry emptyEntry = new ZipEntry(".empty");
        zipOut.putNextEntry(emptyEntry);
        zipOut.closeEntry();
    }
}