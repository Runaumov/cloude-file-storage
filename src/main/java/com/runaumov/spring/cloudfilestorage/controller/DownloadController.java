package com.runaumov.spring.cloudfilestorage.controller;

import com.runaumov.spring.cloudfilestorage.service.ResourceDownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/resource/download")
public class DownloadController {

    private final ResourceDownloadService resourceDownloadService;

    @GetMapping
    public ResponseEntity<byte[]> downLoadResource(@RequestParam String path) {
        byte[] data = resourceDownloadService.resourceDownload(path);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
