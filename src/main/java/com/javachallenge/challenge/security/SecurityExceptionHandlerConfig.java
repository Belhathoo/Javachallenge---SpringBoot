package com.javachallenge.challenge.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SecurityExceptionHandlerConfig {

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("""
                {
                    "timestamp": "%s",
                    "status": 401,
                    "error": "Unauthorized",
                    "message": "Authentication failed: %s",
                    "path": "%s"
                }
                """.formatted(
                    LocalDateTime.now(),
                    authException.getMessage(),
                    request.getRequestURI()
            ));
        };
    }


    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("""
                {
                    "timestamp": "%s",
                    "status": 403,
                    "error": "Forbidden",
                    "message": "Access Denied: %s",
                    "path": "%s"
                }
                """.formatted(
                    LocalDateTime.now(),
                    accessDeniedException.getMessage(),
                    request.getRequestURI()
            ));
        };
    }
}

