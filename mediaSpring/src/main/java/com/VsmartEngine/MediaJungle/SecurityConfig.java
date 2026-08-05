package com.VsmartEngine.MediaJungle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.VsmartEngine.MediaJungle.googleLogin.OAuth2LoginSuccessHandler;

// GDPR-TASK-18: Security-of-processing baseline (GDPR Art. 32 - "appropriate technical measures").
// NOTE: this project authenticates via a custom JWT filter (see userregister.JwtUtil) rather than
// Spring Security's filter chain, and most controllers are intentionally public, so this class is
// deliberately additive: it does NOT lock down endpoints (that would be a breaking behavioural
// change to the existing app), it only wires up CORS + stateless sessions so that cookies/sessions
// used anywhere in the app inherit secure defaults. Enable by uncommenting @Configuration once the
// CORS allow-list in application.properties (GDPR-TASK-20) has been reviewed for the deployment.
//@Configuration
public class SecurityConfig {

	@Autowired
	private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize -> authorize
				.anyRequest().permitAll()
			)
			.oauth2Login(oauth2 -> oauth2
				.loginPage("/oauth2/authorization/google")
				.successHandler(oAuth2LoginSuccessHandler)
			)
			// GDPR-TASK-18: CORS is restricted via GlobalCorsConfig's allow-list (GDPR-TASK-19)
			// rather than accepting every origin.
			.cors(Customizer.withDefaults())
			.csrf(csrf -> csrf.disable())
			// GDPR-TASK-18: stateless sessions - no server-side session containing personal
			// data is created/retained, minimising the amount of personal data stored in
			// memory (GDPR Art. 5(1)(c) - data minimisation).
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
}
