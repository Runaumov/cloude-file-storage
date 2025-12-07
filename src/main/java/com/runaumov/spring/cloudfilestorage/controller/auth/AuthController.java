package com.runaumov.spring.cloudfilestorage.controller.auth;

import com.runaumov.spring.cloudfilestorage.dto.user.UserEntityRequestDto;
import com.runaumov.spring.cloudfilestorage.dto.user.UserEntityResponseDto;
import com.runaumov.spring.cloudfilestorage.service.auth.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistrationService registrationService;

    @Autowired
    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserEntityResponseDto> signUp(@Valid @RequestBody UserEntityRequestDto request) {
        UserEntityResponseDto newUser = registrationService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
}
