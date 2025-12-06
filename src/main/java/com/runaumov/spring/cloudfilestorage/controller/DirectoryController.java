package com.runaumov.spring.cloudfilestorage.controller;

import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.service.DirectoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "directory")
@RequestMapping("/api/directory")
public class DirectoryController {

    private final DirectoryService directoryService;

    @Operation(summary = "Get information about folder")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "500")
    })
    @GetMapping
    public ResponseEntity<List<ResourceResponseDto>> getDirectoryInfo(@RequestParam String path) {
        return ResponseEntity.ok(directoryService.getDirectoryInfo(path));
    }

    @Operation(summary = "Create empty directory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "409"),
            @ApiResponse(responseCode = "500")
    })
    @PostMapping
    public ResponseEntity<ResourceResponseDto> createEmptyDirectory(@RequestParam String path) {
        return ResponseEntity.ok(directoryService.createEmptyDirectory(path));
    }

}