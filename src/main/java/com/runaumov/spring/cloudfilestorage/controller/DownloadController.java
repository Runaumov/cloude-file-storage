package com.runaumov.spring.cloudfilestorage.controller;

import com.runaumov.spring.cloudfilestorage.service.MinioStorageService;
import com.runaumov.spring.cloudfilestorage.service.ResourceDownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/resource/download")
public class DownloadController {

    private final ResourceDownloadService resourceDownloadService;
    private final MinioStorageService minioStorageService;

    @GetMapping
    public ResponseEntity<byte[]> downloadResource(@RequestParam String path) {
        byte[] data = resourceDownloadService.resourceDownload(path);

        String filename;
        String contentType;
        MediaType mediaType;

        if (minioStorageService.isDirectory(path)) {
            filename = getDirectoryName(path) + ".zip";
            mediaType = new MediaType("application", "zip");
        } else {
            filename = Paths.get(path).getFileName().toString();
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentLength(data.length);

        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" +
                        URLEncoder.encode(filename, StandardCharsets.UTF_8));

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    private String getDirectoryName(String path) {
        String cleanPath = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        int lastSlash = cleanPath.lastIndexOf('/');
        return lastSlash >= 0 ? cleanPath.substring(lastSlash + 1) : cleanPath;
    }
}
