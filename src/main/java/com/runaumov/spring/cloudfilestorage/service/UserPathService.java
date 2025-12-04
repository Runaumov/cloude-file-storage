package com.runaumov.spring.cloudfilestorage.service;

import org.springframework.stereotype.Service;

@Service
public class UserPathService {

    public String getUserPrefix(Long userId) {
        return "user-" + userId + "-files/";
    }

    public String addUserPrefix(Long userID, String path) {
        String prefix = getUserPrefix(userID);
        return prefix + path;
    }

    public String removeUserPrefix(Long userId, String path) {
        String prefix = getUserPrefix(userId);
        if (path.startsWith(prefix)) {
            return path.substring(prefix.length());
        }
        return path;
    }
}
