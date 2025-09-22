package com.runaumov.spring.cloudfilestorage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/resource")
public class UploadFileController {

    @PostMapping
    public String uploadFile() {
        return null;
    }
}
