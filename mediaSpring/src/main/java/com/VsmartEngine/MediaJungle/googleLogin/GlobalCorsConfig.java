package com.VsmartEngine.MediaJungle.googleLogin;
<<<<<<< HEAD
=======

import org.springframework.beans.factory.annotation.Value;
>>>>>>> internship/main
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

<<<<<<< HEAD
import java.util.List;

//@Configuration
public class GlobalCorsConfig {

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        // Allow your front end's domain
//        configuration.setAllowedOrigins(List.of("http://localhost:4203"));
//        // Allow all standard HTTP methods
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        // Allow the necessary headers
//        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
//        // Allow credentials (cookies)
//        configuration.setAllowCredentials(true);
//        
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        // Apply this configuration to all endpoints
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
=======
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// ============================================
// SOC 2 Control 11: Encryption in Transit & CORS
// Restrict CORS origins to trusted development & production domains
// ============================================
@Configuration
public class GlobalCorsConfig {

    @Value("${security.cors.allowed-origins:http://localhost:3000,http://localhost:4203,http://localhost:8080,http://127.0.0.1:3000}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (origins.contains("*")) {
            configuration.setAllowedOriginPatterns(List.of("*"));
        } else {
            configuration.setAllowedOrigins(origins);
        }

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
>>>>>>> internship/main
}

