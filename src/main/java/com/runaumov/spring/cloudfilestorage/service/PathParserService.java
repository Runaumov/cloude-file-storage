package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import org.springframework.stereotype.Service;

@Service
public class PathParserService {

    public PathComponents parsePath(String path) {

        if (path == null || path.isEmpty()) {
            return new PathComponents("", "");
        }

        if (path.endsWith("/")) {
            return new PathComponents(path, "");
        }

        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex == -1) {
            return new PathComponents("", path);
        } else {
            String folderName = path.substring(0, lastSlashIndex + 1);
            String fileName = path.substring(lastSlashIndex + 1);
            return new PathComponents(folderName, fileName);
        }
    }

    public String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        return path.endsWith("/") ? path : path + "/";
    }

}
