package com.runaumov.spring.cloudfilestorage.config.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runaumov.spring.cloudfilestorage.entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StreamUtils;
import java.io.IOException;

public class JsonAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonAuthenticationFilter(RequestMatcher requestMatcher, AuthenticationManager authenticationManager) {
        super(requestMatcher);
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        try {
            byte[] bytes = StreamUtils.copyToByteArray(request.getInputStream());

            JsonNode root = objectMapper.readTree(bytes);
            String username = root.path("username").asText("");
            String password = root.path("password").asText("");

            if (username.isEmpty() || password.isEmpty()) {
                throw new BadCredentialsException("Username or password is empty");
            }

            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(username, password);

            return this.getAuthenticationManager().authenticate(token);
        } catch (RuntimeException e) {
            throw new AuthenticationServiceException("Invalid JSON", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write("{\"username\":\"" + authResult.getName() + "\"}");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
