package com.runaumov.spring.cloudfilestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResourceResponseDto {

    private String path;
    private String name;
    private String size;
    private String type;
}
