package com.runaumov.spring.cloudfilestorage.controller.resource;

import com.runaumov.spring.cloudfilestorage.dto.resource.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.service.resource.ResourceDeleteService;
import com.runaumov.spring.cloudfilestorage.service.resource.ResourceInfoService;
import com.runaumov.spring.cloudfilestorage.service.resource.UploadFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Validated
@Tag(name = "resource")
@RequestMapping("/api/resource")
public class ResourceManagementController {

    private final UploadFileService uploadFilesService;
    private final ResourceInfoService resourceInfoService;
    private final ResourceDeleteService resourceDeleteService;

    @Operation(summary = "Get information about resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "500")
    })
    @GetMapping
    public ResponseEntity<ResourceResponseDto> getResourceInfo(@RequestParam String path) {
        return ResponseEntity.ok(resourceInfoService.getResourceInfo(path));
    }

    @Operation(summary = "Upload resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "500")
    })
    @PostMapping
    public ResponseEntity<List<ResourceResponseDto>> uploadFile(
            @RequestParam("path") String path,
            @RequestParam("object") List<MultipartFile> files
    ) {
        List<ResourceResponseDto> uploadFiles = uploadFilesService.uploadFiles(path, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadFiles);
    }

    @Operation(summary = "Delete resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "500")
    })
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResource(@RequestParam String path) {
        resourceDeleteService.deleteResource(path);
    }
}
