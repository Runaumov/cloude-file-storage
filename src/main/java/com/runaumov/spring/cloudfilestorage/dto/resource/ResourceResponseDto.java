package com.runaumov.spring.cloudfilestorage.dto.resource;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
