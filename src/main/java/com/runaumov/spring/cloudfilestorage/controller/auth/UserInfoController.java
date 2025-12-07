package com.runaumov.spring.cloudfilestorage.controller.auth;

import com.runaumov.spring.cloudfilestorage.dto.user.UserEntityResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.user.UserSessionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserInfoController {

    @GetMapping("/me")
    public ResponseEntity<UserEntityResponseDto> getCurrentUser(@AuthenticationPrincipal UserSessionDto userSessionDto) {
        return ResponseEntity.ok().body(new UserEntityResponseDto(userSessionDto.getUsername()));
    }
}
