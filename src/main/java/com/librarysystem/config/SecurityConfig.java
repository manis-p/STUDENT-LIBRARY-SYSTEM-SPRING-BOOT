package com.librarysystem.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.librarysystem.security.filter.JwtFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enable @PreAuthorize, @PostAuthorize etc.
public class SecurityConfig {

	@Autowired
	private JwtFilter jwtFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeHttpRequests()

				// Public endpoints
				.requestMatchers("/api/user/signup", "/api/user/verify-otp", "/api/user/login", "/api/user/forgot-password",
						"/api/user/reset-password",
						// Swagger ke URLs
						"/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html","/api/user/profile")
				.permitAll()

				// Admin protected endpoints
				.requestMatchers("/api/admin/**").hasRole("ADMIN")

				// Authenticated user endpoints
				.requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")

				// Any other request
				.anyRequest().authenticated()

				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		// Add JWT filter
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
