package com.apuntame.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);

        config.addAllowedOriginPattern("http://localhost");
        config.addAllowedOriginPattern("http://localhost:*");
        config.addAllowedOriginPattern("http://127.0.0.1");
        config.addAllowedOriginPattern("http://127.0.0.1:*");
        config.addAllowedOriginPattern("http://192.168.*.*");
        config.addAllowedOriginPattern("http://192.168.*.*:*");
        config.addAllowedOriginPattern("http://10.*.*.*");
        config.addAllowedOriginPattern("http://10.*.*.*:*");

        config.addAllowedHeader("*");

        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("PATCH");

        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Type");

        // Apply CORS configuration to all API routes
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
