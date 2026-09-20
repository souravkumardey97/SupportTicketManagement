package com.supportticket.config;

import com.supportticket.security.JwtAuthenticationFilter;
import com.supportticket.security.JwtService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtSecurityConfig {

    @Bean
    @ConditionalOnBean(JwtService.class)
    JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
        return new JwtAuthenticationFilter(jwtService);
    }
}
