package com.runaumov.spring.cloudfilestorage.dto;

import com.runaumov.spring.cloudfilestorage.model.ResourceType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceResponseDirectoryDto {
    private String path;
    private String name;
    private String type;
}


