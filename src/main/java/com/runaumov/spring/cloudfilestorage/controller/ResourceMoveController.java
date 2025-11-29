package com.runaumov.spring.cloudfilestorage.controller;

import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.service.ResourceMoveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/resource/move")
public class ResourceMoveController {

    private final ResourceMoveService resourceMoveService;

    @GetMapping
    public ResponseEntity<ResourceResponseDto> moveResource(@RequestParam("from") String from, @RequestParam("to") String to) {
        return ResponseEntity.ok(resourceMoveService.resourceMove(from, to));
    }
}
