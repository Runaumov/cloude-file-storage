package com.runaumov.spring.cloudfilestorage.controller;

import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.service.ResourceSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Validated
@Tag(name = "resource search")
@RequestMapping("/api/resource/search")
public class ResourceSearchController {

    private final ResourceSearchService resourceSearchService;

    @Operation(summary = "Search resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "500")
    })
    @GetMapping
    public ResponseEntity<List<ResourceResponseDto>> searchResource(@RequestParam String query) throws Exception {
        return ResponseEntity.ok(resourceSearchService.searchResource(query));
    }
}
