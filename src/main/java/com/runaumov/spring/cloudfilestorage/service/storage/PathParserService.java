package com.runaumov.spring.cloudfilestorage.service.storage;

import com.runaumov.spring.cloudfilestorage.dto.path.PathComponents;
import org.springframework.stereotype.Service;

@Service
public class PathParserService {

    public PathComponents parsePath(String path) {
        if (path == null || path.isEmpty()) {
            return new PathComponents("", "");
        }

        boolean isDir = path.endsWith("/");
        String normalizedPath = isDir ? path.substring(0, path.length() - 1) : path;

        int lastSlashIndex = normalizedPath.lastIndexOf('/');

        String folderPath;
        String name;

        if (lastSlashIndex == -1) {
            folderPath = "";
            name = isDir ? normalizedPath + "/" : normalizedPath;
        } else {
            folderPath = normalizedPath.substring(0, lastSlashIndex + 1);
            name = normalizedPath.substring(lastSlashIndex + 1) + (isDir ? "/" : "");
        }

        return new PathComponents(folderPath, name);
    }

    public String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        return path.endsWith("/") ? path : path + "/";
    }

    public boolean isDirectory(String path) {
        return path != null && path.endsWith("/");
    }

}
