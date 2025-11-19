package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
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

    public byte[] resourceDownload(String path) {
        return isDirectory(path) ? downloadDirectory(path) : downloadFile(path);
    }

    private boolean isDirectory(String path) {
        return path.endsWith("/");
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
            String normalizedPath = pathParserService.normalizePath(path);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try (ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {
                Iterable<Result<Item>> items = minioStorageService.listDirectoryItems(normalizedPath, true);

                for (Result<Item> result : items) {
                    Item item = result.get();
                    String objectName = item.objectName();
                    addObjectToZip(normalizedPath, objectName, zipOut);
                }
            }

            return byteArrayOutputStream.toByteArray();
        }, "Failed to download directory: "  + path);
    }

    private void addObjectToZip(String basePath, String objectName, ZipOutputStream zipOut) throws Exception {
        try (InputStream inputStream = minioStorageService.getObjectStream(objectName)) {
            String relativePath = objectName.substring(basePath.length());

            ZipEntry entry = new ZipEntry(relativePath);
            zipOut.putNextEntry(entry);

            inputStream.transferTo(zipOut);

            zipOut.closeEntry();
        }
    }
}
