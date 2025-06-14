package com.librarysystem.security.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.librarysystem.model.RefreshToken;
import com.librarysystem.model.User;
import com.librarysystem.repository.RefreshTokenRepository;
import com.librarysystem.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private UserRepository userRepository;

	private Long refreshTokenDurationMs = 6000000L;

	// Generate and save new refresh token for user (multi-device)
	public RefreshToken createRefreshToken(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		refreshTokenRepository.findAllByUser(user).stream()
				.filter(token -> token.getExpiryDate().isBefore(Instant.now()))
				.forEach(token -> refreshTokenRepository.delete(token));

		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUser(userRepository.findById(userId).get());
		refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
		refreshToken.setToken(UUID.randomUUID().toString());
		return refreshTokenRepository.save(refreshToken);
	}

	public boolean isValid(String token) {
		return refreshTokenRepository.findByToken(token).filter(rt -> rt.getExpiryDate().isAfter(Instant.now()))
				.isPresent();
	}

	// Validate token (check expiry)
	public boolean isTokenExpired(RefreshToken token) {
		return token.getExpiryDate().isBefore(Instant.now());
	}

	// Delete refresh token by token string (logout current device)
	@Transactional
	public void deleteByToken(String token) {
		refreshTokenRepository.deleteByToken(token);
	}

	// Delete all refresh tokens for a user (logout all devices)
	@Transactional
	public int deleteByUser(User user) {
		return refreshTokenRepository.deleteByUser(user);
	}

	// Find token by string
	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	// Find all tokens of a user (to check multiple devices)
	public List<RefreshToken> findAllByUser(User user) {
		return refreshTokenRepository.findAllByUser(user);
	}

	// find the user id by using of refreher token
	public String getUserEmailFromToken(String token) {

		RefreshToken refreshToken = findByToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));
		return refreshToken.getUser().getEmail();

	}

}
