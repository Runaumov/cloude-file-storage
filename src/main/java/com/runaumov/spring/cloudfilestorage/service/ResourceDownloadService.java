package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import com.runaumov.spring.cloudfilestorage.util.MinioUtils;
import io.minio.Result;
import io.minio.StatObjectResponse;
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

    public byte[] resourceDownload(String path) {

        if (path.endsWith("/")) {
            return downloadDirectory(path);
        } else {
            return downloadFile(path);
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
                Iterable<Result<Item>> items = minioStorageService.listDirectoryItems(path, true);

                boolean hasEntries = false;

                for (Result<Item> result : items) {
                    Item item = result.get();
                    String objectName = item.objectName();

                    String relativePath = objectName.substring(path.length());
                    if (relativePath.startsWith("/")) {
                        relativePath = relativePath.substring(1);
                    }

                    if (objectName.endsWith("/")) {
                        ZipEntry entry = new ZipEntry(relativePath + "/");
                        zipOut.putNextEntry(entry);
                        zipOut.closeEntry();
                        hasEntries = true;
                        continue;
                    }

                    addObjectToZip(objectName, relativePath, zipOut);
                    hasEntries = true;
                }

                if (!hasEntries) {
                    ZipEntry emptyEntry = new ZipEntry(".empty");
                    zipOut.putNextEntry(emptyEntry);
                    zipOut.closeEntry();
                }
            }

            return byteArrayOutputStream.toByteArray();
        }, "Failed to download directory: " + path);
    }

    private void addObjectToZip(String objectName, String relativePath, ZipOutputStream zipOut) throws Exception {
        try (InputStream inputStream = minioStorageService.getObjectStream(objectName)) {
            ZipEntry entry = new ZipEntry(relativePath);
            zipOut.putNextEntry(entry);
            inputStream.transferTo(zipOut);
            zipOut.closeEntry();
        }
    }
}
