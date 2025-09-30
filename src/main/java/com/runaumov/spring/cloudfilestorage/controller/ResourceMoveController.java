package com.runaumov.spring.cloudfilestorage.controller;

import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/resource/move")
public class ResourceMoveController {

    private final ResourceMoveService resourceMoveService;

    public ResponseEntity<ResourceResponseDto> moveResource(@RequestParam("from") String from, @RequestParam("to") String to) {
        return ResponseEntity.ok(resourceMoveService.resourceMove(from, to));

    }
}
