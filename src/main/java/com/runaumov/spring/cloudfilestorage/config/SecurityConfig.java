package com.runaumov.spring.cloudfilestorage.config;

import com.runaumov.spring.cloudfilestorage.config.filter.JsonAuthenticationFilter;
import com.runaumov.spring.cloudfilestorage.security.UserEntityDetails;
import com.runaumov.spring.cloudfilestorage.service.UserEntityDetailService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.PathPatternRequestMatcherBuilderFactoryBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserEntityDetailService userEntityDetailService;

    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            PasswordEncoder encoder) throws Exception {

        AuthenticationManagerBuilder authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authManagerBuilder
                .userDetailsService(userEntityDetailService)
                .passwordEncoder(encoder);

        return authManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {

        RequestMatcher requestMatcher = request -> "POST".equals(request.getMethod()) &&
                "/auth/sign-in".equals(request.getRequestURI());

        JsonAuthenticationFilter jsonAuthenticationFilter =
                new JsonAuthenticationFilter(requestMatcher, authenticationManager);

        httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterAt(jsonAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
