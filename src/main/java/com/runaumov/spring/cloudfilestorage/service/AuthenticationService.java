package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.exception.UserUnautorizedException;
import com.runaumov.spring.cloudfilestorage.security.UserEntityDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserUnautorizedException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return extractUserId(userDetails);
        }
        throw new UserUnautorizedException("User not authenticated");
    }

    private Long extractUserId(UserDetails userDetails) {
        if (userDetails instanceof UserEntityDetails) {
            return ((UserEntityDetails) userDetails).getId();
        }
        throw new UserUnautorizedException("Cannot extract user ID");
    }
}
