package com.runaumov.spring.cloudfilestorage.controller;

import com.runaumov.spring.cloudfilestorage.dto.UserEntityRequestDto;
import com.runaumov.spring.cloudfilestorage.dto.UserEntityResponseDto;
import com.runaumov.spring.cloudfilestorage.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // TODO возможно стоит написать функциональный интерфейс, если ловить еще 400
    @PostMapping("/sign-up")
    public ResponseEntity<UserEntityResponseDto> signUp(@RequestBody UserEntityRequestDto request) {
        UserEntityResponseDto newUser = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

//    @PostMapping("/sign-out")
//    public ResponseEntity<Void> logout(HttpServletRequest request) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null) {
//            return ResponseEntity.status(401).build();
//        }
//
//        new SecurityContextLogoutHandler().logout(request, null, auth);
//        return ResponseEntity.noContent().build();
//    }
}
