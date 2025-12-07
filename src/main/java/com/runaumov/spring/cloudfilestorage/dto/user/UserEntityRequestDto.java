package com.runaumov.spring.cloudfilestorage.dto.user;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntityRequestDto {

    @Size(min = 3, max = 25, message = "Username must be between 3 and 25 characters")
    private String username;
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
