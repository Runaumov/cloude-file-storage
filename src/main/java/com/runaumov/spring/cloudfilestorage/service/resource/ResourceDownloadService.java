package com.runaumov.spring.cloudfilestorage.service.resource;

import com.runaumov.spring.cloudfilestorage.service.auth.storage.MinioStorageService;
import com.runaumov.spring.cloudfilestorage.service.auth.storage.PathParserService;
import com.runaumov.spring.cloudfilestorage.service.user.UserContextService;
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
    private final PathParserService pathParserService;
    private final UserContextService userContextService;

    public byte[] resourceDownload(String path) {

        String userPath = userContextService.addUserPrefix(path);

        if (pathParserService.isDirectory(userPath)) {
            if (!userContextService.isUserRoot(userPath)) {
                MinioValidator.verificationDirectory(
                        userPath,
                        () -> minioStorageService.getStatObject(userPath),
                        () -> minioStorageService.listDirectoryItems(userPath, false),
                        "Folder not found: " + path);
            }
            return downloadDirectory(userPath);
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

    private byte[] downloadDirectory(String path) {
        return MinioUtils.handleMinioException(() -> {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try (ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {
                if (hasItems(path)) {
                    addItemsToZip(path, zipOut);
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

    private void addItemsToZip(String path, ZipOutputStream zipOut) throws Exception {
        Iterable<Result<Item>> items = minioStorageService.listDirectoryItems(path, true);

        for (Result<Item> result : items) {
            Item item = result.get();
            String objectName = item.objectName();

            String pathWithoutPrefix = userContextService.removeUserPrefix(objectName);
            String relativePath = calculateRelativePath(userContextService.removeUserPrefix(path), pathWithoutPrefix);

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