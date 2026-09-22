package com.shieldflow.claim.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf().disable()
            .cors().and()
            .headers().frameOptions().disable().and() // Allow H2 Console frames
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
            .antMatchers("/api/auth/**").permitAll()
            .antMatchers("/h2-console/**").permitAll()
            .antMatchers("/static/**", "/", "/assets/**", "/favicon.ico", "/index.html").permitAll()
            // We permit endpoints globally in Spring Security and handle token authentication 
            // inside our controllers via our JwtUtil helper. This allows us to return precise, 
            // clean JSON error payloads (e.g. 401 Unauthorized) that Vue 3 can easily render, 
            // bypassing standard spring security lockout redirection pages.
            .anyRequest().permitAll()

        return http.build()
    }
}
