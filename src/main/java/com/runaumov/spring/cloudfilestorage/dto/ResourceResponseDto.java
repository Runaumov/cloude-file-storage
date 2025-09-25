package com.runaumov.spring.cloudfilestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResourceResponseDto {

    private String path;
    private String name;
    private Long size;
    private String type;

    public ResourceResponseDto(String path, String name, String type) {
        this.path = path;
        this.name = name;
        this.type = type;
    }
}
