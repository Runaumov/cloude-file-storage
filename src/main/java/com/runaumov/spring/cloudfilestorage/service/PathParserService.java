package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import org.springframework.stereotype.Service;

@Service
public class PathParserService {

    public PathComponents parsePath(String path) {
        if (path == null || path.isEmpty()) {
            return new PathComponents("", "");
        }

        // TODO : возможно стоит вынести в отдельные методы
        if (path.endsWith("/")) {

            String pathWithoutTrailingSlash = path.substring(0, path.length() - 1);
            int lastSlashIndex = pathWithoutTrailingSlash.lastIndexOf('/');

            if (lastSlashIndex == -1) {
                return new PathComponents(path, path);
            } else {
                String name = pathWithoutTrailingSlash.substring(lastSlashIndex + 1) + "/";
                return new PathComponents(path, name);
            }
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
