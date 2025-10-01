package com.runaumov.spring.cloudfilestorage.controller;

import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.service.ResourceSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/resource/search")
public class ResourceSearchController {

    private final ResourceSearchService resourceSearchService;

    public ResponseEntity<ResourceResponseDto> searchResource(@RequestParam String query) {
        return ResponseEntity.ok(resourceSearchService.searchResource(query));
    }
}
