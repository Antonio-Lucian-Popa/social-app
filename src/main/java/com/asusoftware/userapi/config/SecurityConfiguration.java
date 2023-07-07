package com.asusoftware.userapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.disable())
                //.authorizeHttpRequests()
                .securityMatcher("/api/**") //Configure HttpSecurity to only be applied to URLs that start with /api/
                .authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers("/api/v1/auth/**").permitAll()
                                .anyRequest()
                                .permitAll()
                )
                //.requestMatchers("/api/v1/auth/**") //authorize only the /api/v1/auth/** endpoints
                //.permitAll()
                //.anyRequest()
               // .authenticated()
               // .and()
               // .sessionManagement()
                .sessionManagement((sessionManagement) ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                //.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // spring will create a new session for every request
                //.and()
                .authenticationProvider(authenticationProvider) // represents the authentication provider
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // represents the authentication filter
        return http.build();
    }
}
