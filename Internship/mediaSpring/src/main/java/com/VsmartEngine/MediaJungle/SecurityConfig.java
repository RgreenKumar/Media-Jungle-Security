<<<<<<< HEAD
package com.VsmartEngine.MediaJungle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.VsmartEngine.MediaJungle.googleLogin.OAuth2LoginSuccessHandler;

//@Configuration
public class SecurityConfig {
	
//	 @Autowired
//	    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
//	 
//	 
//
//	 @Bean
//	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//	        http
//	            // Public endpoints
//	            .authorizeHttpRequests(authorize -> authorize
////	                .requestMatchers("/", "/login**", "/error").permitAll()
//	                // All other endpoints require authentication
////	                .anyRequest().authenticated()
//	            		.anyRequest().permitAll()
//	            )
//	            // OAuth2 login configuration
//	            .oauth2Login(oauth2 -> oauth2
//	                .loginPage("/oauth2/authorization/google")
//	                .successHandler(oAuth2LoginSuccessHandler)
//	            )
//	            // Enable CORS if needed
//	            .cors(Customizer.withDefaults())
//	            // Disable CSRF for simplicity (adjust for production)
//	            .csrf(csrf -> csrf.disable())
//	            // Use stateful session management (so that the user is stored in session)
//	            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
//
//	        return http.build();
//	    }
	}

=======
// =======================================
// Internship Security Enhancement
// Feature: Role Based Access Control
// ISO27001 Control: Access Control
// =======================================
package com.VsmartEngine.MediaJungle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.VsmartEngine.MediaJungle.security.CustomAccessDeniedHandler;
import com.VsmartEngine.MediaJungle.security.JwtAuthenticationFilter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// RBAC: Enable Spring Security and Method Security
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    // Constructor Injection
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Enable CORS with default settings
            .cors(Customizer.withDefaults())
            // Disable CSRF for stateless REST API
            .csrf(csrf -> csrf.disable())
            // Stateless Session Management
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // RBAC: Endpoint Authorization Rules
            .authorizeHttpRequests(authorize -> authorize
                // Public APIs (Permit All)
                .requestMatchers(
                    "/api/v2/userregister",
                    "/api/v2/login",
                    "/api/v2/send-code",
                    "/api/v2/send-code/**",
                    "/api/v2/verify-code",
                    "/sendCodewhileRegister",
                    "/verify-code",
                    "/api/v2/checkAdminRole",
                    "/api/v2/count",
                    "/api/v2/dashstatus/**",
                    "/api/v2/security-standards/**",
                    "/api/v2/mfa/verify-login"
                ).permitAll()
                // All other endpoints permit all by default, enforced via @PreAuthorize method security
                .anyRequest().permitAll()
            )
            // RBAC: Custom Exception Handling for HTTP 403 Access Denied
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedHandler(customAccessDeniedHandler)
            )
            // ============================================
            // Internship Security Enhancement
            // Feature : HTTP Security Headers
            // ISO27001 Control : Secure Configuration
            // OWASP Secure Headers
            // ============================================
            .headers(headers -> {
                // Prevent Clickjacking (X-Frame-Options: DENY)
                headers.frameOptions(frame -> frame.deny());
                // Prevent MIME sniffing (X-Content-Type-Options: nosniff)
                headers.contentTypeOptions(Customizer.withDefaults());
                // Content Security Policy (CSP)
                headers.contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                        "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                        "img-src 'self' data: blob: http: https:; " +
                        "font-src 'self' data: https://fonts.gstatic.com; " +
                        "media-src 'self' blob: http: https:; " +
                        "connect-src 'self' http: https: ws: wss:; " +
                        "frame-ancestors 'none';")
                );
                // Referrer Policy: strict-origin-when-cross-origin
                headers.referrerPolicy(referrer -> referrer
                    .policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                );
                // Restrict browser permissions (Permissions-Policy)
                headers.permissionsPolicy(permissions -> permissions
                    .policy("camera=(), microphone=(), geolocation=(), payment=(), usb=()")
                );
                // Enable HSTS (Strict-Transport-Security: max-age=31536000; includeSubDomains; preload)
                headers.httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)
                    .preload(true)
                );
            })
            // RBAC: Register JWT Authentication Filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ============================================
    // SOC 2 Control 1: Secure Password Storage
    // Centralized BCrypt PasswordEncoder Bean
    // ============================================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
>>>>>>> internship/main
