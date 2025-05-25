package com.librarysystem.security.filter;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.librarysystem.security.service.CustomUserDetailsService;
import com.librarysystem.util.JwtUtil;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	
	

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException, java.io.IOException {
		
		
		String path = request.getRequestURI();
		System.out.println("Request URI: " + path);
		System.out.println("Authorization Header: " + request.getHeader("Authorization"));
		// Skip JWT filter for these public endpoints
		if (path.startsWith("/api/user/signup") ||
			path.startsWith("/api/user/login") ||
			path.startsWith("/api/user/forgot-password") ||
			path.startsWith("/api/user/reset-password")) {
			System.out.println("Skipping JWT Filter for this path");
			filterChain.doFilter(request, response);
			return;
		}


		final String authHeader = request.getHeader("Authorization");
		String email = null;
		String jwt = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			jwt = authHeader.substring(7);
			email = jwtUtil.extractUsername(jwt);
		}

		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
			if (jwtUtil.validateToken(jwt, userDetails)) {
				String role = jwtUtil.extractRole(jwt).name();
				SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

				UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null,
						Collections.singleton(authority));
				SecurityContextHolder.getContext().setAuthentication(token);
			}
		}

		filterChain.doFilter(request, response);
	}

}
