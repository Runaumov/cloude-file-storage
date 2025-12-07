package com.runaumov.spring.cloudfilestorage.controller.resource;

import com.runaumov.spring.cloudfilestorage.dto.resource.ResourceResponseDto;
import com.runaumov.spring.cloudfilestorage.service.resource.ResourceMoveService;
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

@Controller
@RequiredArgsConstructor
@Validated
@Tag(name = "resource move")
@RequestMapping("/api/resource/move")
public class ResourceMoveController {

    private final ResourceMoveService resourceMoveService;

    @Operation(summary = "Move resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "500")
    })
    @GetMapping
    public ResponseEntity<ResourceResponseDto> moveResource(@RequestParam("from") String from, @RequestParam("to") String to) {
        return ResponseEntity.ok(resourceMoveService.resourceMove(from, to));
    }
}
