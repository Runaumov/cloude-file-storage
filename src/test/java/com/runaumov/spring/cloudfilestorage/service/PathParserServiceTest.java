package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.PathComponents;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PathParserServiceTest {

    private final PathParserService pathParserService = new PathParserService();

    @Test
    void shouldReturnPathComponents_whenFolderExist() {
        String path = "folder/";

        PathComponents pathComponents = pathParserService.parsePath(path);

        Assertions.assertEquals("", pathComponents.path());
        Assertions.assertEquals("folder/", pathComponents.name());
    }

    @Test
    void shouldReturnPathComponents_whenSubfolderExist() {
        String path = "folder/subfolder/";

        PathComponents pathComponents = pathParserService.parsePath(path);

        Assertions.assertEquals("folder/", pathComponents.path());
        Assertions.assertEquals("subfolder/", pathComponents.name());
    }

    @Test
    void shouldReturnPathComponents_whenFileExist() {
        String path = "folder/subfolder/file.txt";

        PathComponents pathComponents = pathParserService.parsePath(path);

        Assertions.assertEquals("folder/subfolder/", pathComponents.path());
        Assertions.assertEquals("file.txt", pathComponents.name());
    }

    @Test
    void shouldReturnPathComponents_whenRootFileExist() {
        String path = "/file.txt";

        PathComponents pathComponents = pathParserService.parsePath(path);

        Assertions.assertEquals("/", pathComponents.path());
        Assertions.assertEquals("file.txt", pathComponents.name());
    }
}