package com.VsmartEngine.MediaJungle.googleLogin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

// GDPR-TASK-19: Explicit CORS allow-list (GDPR Art. 32 - security of processing). Several
// controllers currently use a bare @CrossOrigin() (wildcard, allows any origin to call APIs
// that return personal data) - this bean provides the safe, restricted default that new code
// should rely on, and existing @CrossOrigin() annotations should be migrated to remove their
// wildcard over time. The allow-list is externalised to application.properties so it can differ
// per environment instead of being hard-coded (GDPR-TASK-20).
//@Configuration
public class GlobalCorsConfig {

    @Value("${gdpr.cors.allowed-origins:http://localhost:3000,http://localhost:4203}")
    private String allowedOriginsCsv;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Only the front-end origins configured for this deployment may call the API -
        // never "*" for endpoints that return personal data.
        configuration.setAllowedOrigins(Arrays.asList(allowedOriginsCsv.split(",")));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
