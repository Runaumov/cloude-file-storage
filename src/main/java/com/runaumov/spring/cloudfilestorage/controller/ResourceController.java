package com.runaumov.spring.cloudfilestorage.controller;

import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.service.ResourceInfoService;
import com.runaumov.spring.cloudfilestorage.service.UploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController {

    private final UploadFileService uploadFilesService;
    private final ResourceInfoService resourceInfoService;

    @GetMapping
    public ResponseEntity<ResourceResponseDto> getResourceInfo(@RequestParam String path) {
        return ResponseEntity.ok(resourceInfoService.getResourceInfo(path));
    }

    @PostMapping
    public ResponseEntity<List<ResourceResponseDto>> uploadFile(
            @RequestParam("path") String path,
            @RequestParam("file") List<MultipartFile> files
    ) {
        List<ResourceResponseDto> uploadFiles = uploadFilesService.uploadFiles(path, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadFiles);
    }
}
