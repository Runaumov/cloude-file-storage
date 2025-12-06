package com.runaumov.spring.cloudfilestorage.controller;

import com.runaumov.spring.cloudfilestorage.ValidPath;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDirectoryDto;
import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.service.DirectoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/directory")
public class DirectoryController {

    private final DirectoryService directoryService;

    @GetMapping
    public ResponseEntity<List<ResourceResponseDto>> getDirectoryInfo(@RequestParam String path) {
        return ResponseEntity.ok(directoryService.getDirectoryInfo(path));
    }

    @PostMapping
    public ResponseEntity<ResourceResponseDto> createEmptyDirectory(@RequestParam String path) {
        return ResponseEntity.ok(directoryService.createEmptyDirectory(path));
    }

}
