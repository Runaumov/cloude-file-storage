package com.runaumov.spring.cloudfilestorage.controller.resource;

import com.runaumov.spring.cloudfilestorage.service.resource.ResourceDownloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "resource download")
@RequestMapping("/api/resource/download")
public class ResourceDownloadController {

    private final ResourceDownloadService resourceDownloadService;

    @Operation(summary = "Download resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "500")
    })
    @GetMapping
    public ResponseEntity<byte[]> downLoadResource(@RequestParam String path) {
        byte[] data = resourceDownloadService.resourceDownload(path);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
