package com.runaumov.spring.cloudfilestorage.controller;

import com.runaumov.spring.cloudfilestorage.dto.UserEntityResponseDto;
import com.runaumov.spring.cloudfilestorage.dto.UserSessionDto;
import com.runaumov.spring.cloudfilestorage.security.UserEntityDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/user")
public class UserInfoController {

    @GetMapping("/me")
    public ResponseEntity<UserEntityResponseDto> getCurrentUser(@AuthenticationPrincipal UserSessionDto userSessionDto) {
        return ResponseEntity.ok().body(new UserEntityResponseDto(userSessionDto.getUsername()));
    }
}
