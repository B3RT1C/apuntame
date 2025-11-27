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

        // Permitir credenciales (cookies, tokens de autenticación)
        config.setAllowCredentials(true);

        // Orígenes permitidos - Solo red local (SEGURO)
        config.addAllowedOriginPattern("http://localhost:*");      // Desarrollo local
        config.addAllowedOriginPattern("http://127.0.0.1:*");      // Desarrollo local (alternativo)
        config.addAllowedOriginPattern("http://192.168.*.*:*");    // Red WiFi local (producción)
        config.addAllowedOriginPattern("http://10.*.*.*:*");       // Otra configuración de red local

        // Headers permitidos - Permitir todos los headers comunes
        config.addAllowedHeader("*");

        // Métodos HTTP permitidos - Todos los métodos REST estándar
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("PATCH");

        // Headers expuestos - Headers que el frontend puede leer de la respuesta
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Type");

        // Aplicar configuración CORS a todas las rutas de la API
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
