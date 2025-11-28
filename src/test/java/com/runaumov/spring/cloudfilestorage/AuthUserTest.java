package com.runaumov.spring.cloudfilestorage;

import com.runaumov.spring.cloudfilestorage.dto.UserEntityRequestDto;
import com.runaumov.spring.cloudfilestorage.dto.UserSessionDto;
import com.runaumov.spring.cloudfilestorage.entity.UserEntity;
import com.runaumov.spring.cloudfilestorage.security.UserEntityDetails;
import com.runaumov.spring.cloudfilestorage.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class AuthUserTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("password");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void resetDb() {
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldAuthorizedUser_whenCredentialsAreValid() throws Exception {
        String json = "{\"username\":\"testuser\",\"password\":\"password\"}";
        authService.registerUser(new UserEntityRequestDto("testuser", "password"));

        mockMvc.perform(post("/auth/sign-in").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void shouldReturn401_whenPasswordWrong() throws Exception {
        String json = "{\"username\":\"testuser\",\"password\":\"wrong\"}";

        mockMvc.perform(post("/auth/sign-in").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldGetUserEntityResponseDto_whenCredentialsAreValid() throws Exception {
        String json = "{\"username\":\"testuser\",\"password\":\"password\"}";

        mockMvc.perform(post("/auth/sign-up").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void shouldReturn409_whenUsernameAlreadyExists() throws Exception {
        String json = "{\"username\":\"testuser\",\"password\":\"password\"}";

        authService.registerUser(new UserEntityRequestDto("testuser", "password"));

        mockMvc.perform(post("/auth/sign-up").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn204_whenUserLogout() throws Exception {
        mockMvc.perform(post("/auth/sign-out")
                        .with(user("testuser")))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn401_whenUserNotAuthorized() throws Exception {
        mockMvc.perform(post("/auth/sign-out"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldGetUserEntityResponseDto_whenUserAuthorized() throws Exception {
        UserSessionDto sessionDto = new UserSessionDto(1L, "username");

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(sessionDto, null, List.of());

        mockMvc.perform(get("/user/me")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("username"));
    }

    @Test
    void shouldGet401_whenUserUnauthorized() throws Exception {
        mockMvc.perform(get("/user/me")).andExpect(status().isUnauthorized());
    }
}
