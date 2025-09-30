package com.runaumov.spring.cloudfilestorage.controller;

import com.runaumov.spring.cloudfilestorage.service.ResourceDownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.nio.file.Paths;

@Controller
@RequiredArgsConstructor
@RequestMapping("/resource/download")
public class DownloadController {

    private final ResourceDownloadService resourceDownloadService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> downLoadResource(@RequestParam String path) {
        byte[] data = resourceDownloadService.resourceDownload(path);
        String filename = path.endsWith("/") ? "archive.zip" : Paths.get(path).getFileName().toString();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(data);
    }
}
