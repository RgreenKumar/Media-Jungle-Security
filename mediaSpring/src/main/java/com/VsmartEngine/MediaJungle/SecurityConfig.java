package com.VsmartEngine.MediaJungle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.VsmartEngine.MediaJungle.googleLogin.OAuth2LoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	 @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http
	            .cors(cors -> cors.configurationSource(request -> {
	                var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
	                corsConfiguration.setAllowedOriginPatterns(java.util.List.of("*"));
	                corsConfiguration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	                corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
	                corsConfiguration.setAllowCredentials(true);
	                return corsConfiguration;
	            }))
	            .csrf(csrf -> csrf.disable())
	            .authorizeHttpRequests(authorize -> authorize
	                .anyRequest().permitAll()
	            )
	            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

	        return http.build();
	    }
	}

