package com.runaumov.spring.cloudfilestorage.service.user;

import com.runaumov.spring.cloudfilestorage.service.auth.AuthenticationContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserContextService {
    private final AuthenticationContextService authenticationContextService;
    private final UserPathService userPathService;

    public Long getCurrentUserId() {
        return authenticationContextService.getCurrentUserId();
    }

    public String getUserPrefix() {
        return userPathService.getUserPrefix(getCurrentUserId());
    }

    public String addUserPrefix(String path) {
        return userPathService.addUserPrefix(getCurrentUserId(), path);
    }

    public String removeUserPrefix(String path) {
        return userPathService.removeUserPrefix(getCurrentUserId(), path);
    }

    public boolean isUserRoot(String path) {
        return path.equals(getUserPrefix());
    }
}
