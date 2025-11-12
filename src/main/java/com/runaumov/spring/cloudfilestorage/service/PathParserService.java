package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import org.springframework.stereotype.Service;

@Service
public class PathParserService {

    public PathComponents parsePath(String path) {
        String normalPath = normalizePath(path);
        String pathWithoutTrailingSlash = removeTrailingSlash(normalPath);

        int lastSlashIndex = pathWithoutTrailingSlash.lastIndexOf('/');

        if (lastSlashIndex == -1) {
            return new PathComponents("", pathWithoutTrailingSlash);
        }
        return new PathComponents(pathWithoutTrailingSlash.substring(0, lastSlashIndex + 1), pathWithoutTrailingSlash.substring(lastSlashIndex + 1));
    }

    public String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        return path.endsWith("/") ? path : path + "/";
    }

    private String removeTrailingSlash(String path) {
        return path.endsWith("/") && !path.isEmpty() ? path.substring(0, path.length() - 1) : path;
    }
}
