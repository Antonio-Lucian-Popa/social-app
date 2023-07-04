package com.asusoftware.userapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request, // represent the request of the client
            @NotNull HttpServletResponse response, // represent the response of the server
            @NotNull FilterChain filterChain // chain of filters
    ) throws ServletException, IOException {
      // Check if we have the jwt token in the header
        final String authorizationHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Extract the jwt token from the header
        jwt = authorizationHeader.substring(7); // we have position seven because we have "Bearer "
        userEmail = jwtService.extractUsername(jwt); // extract the user email from the jwt token;
    }
}