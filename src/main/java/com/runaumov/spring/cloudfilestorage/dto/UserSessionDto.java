package com.runaumov.spring.cloudfilestorage.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserSessionDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Long id;
    private final String username;

    public UserSessionDto(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
