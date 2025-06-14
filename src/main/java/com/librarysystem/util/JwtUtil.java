package com.librarysystem.util;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.librarysystem.model.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {

	private static final String SECRET_KEY = "your256bitlongsecretkeyyour256bitlongsecretkey";

	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(String email, Role role) {
		return Jwts.builder().setSubject(email).claim("role", role.name())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 2)) // 2 minutes
				.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
	}

	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		final String email = extractUsername(token);
		return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	public Role extractRole(String token) {
		String roleString = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody()
				.get("role", String.class); // Extract role as string from claims

		// Convert the role string into the Role enum (USER_ROLE or ADMIN_ROLE)
		return Role.valueOf(roleString);

	}

	public Instant getExpiryFromToken(String token) {
		Date expirationDate = extractAllClaims(token).getExpiration();
		return expirationDate.toInstant(); // convert java.util.Date to java.time.Instant
	}

	public String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private boolean isTokenExpired(String token) {
		return extractAllClaims(token).getExpiration().before(new Date());
	}

	private Claims extractAllClaims(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException e) {

			System.out.println("Access Token expired: " + e.getMessage());
			throw new RuntimeException("Access Token expired");
		}
	}

}
