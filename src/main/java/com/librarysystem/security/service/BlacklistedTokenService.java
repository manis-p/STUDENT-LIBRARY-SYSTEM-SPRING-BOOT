package com.librarysystem.security.service;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.librarysystem.model.BlacklistedToken;
import com.librarysystem.repository.BlacklistedTokenRepository;

@Service
public class BlacklistedTokenService {
	@Autowired
	private BlacklistedTokenRepository blacklistedTokenRepository;

	public void blacklistToken(String token, Instant expiryDate) {
		BlacklistedToken blacklistedToken = new BlacklistedToken();
		blacklistedToken.setBlacklistedToken(token);
		blacklistedToken.setExpiryDate(expiryDate);
		blacklistedTokenRepository.save(blacklistedToken);
	}

	public boolean isTokenBlacklisted(String token) {
		return blacklistedTokenRepository.existsByBlacklistedToken(token);
	}

	@Scheduled(cron = "0 * * * * *")
	public void removeExpiredTokens() {
		Instant now = Instant.now();
		//System.out.println("this this run with every minute" + now);
		List<BlacklistedToken> expired = blacklistedTokenRepository.findAll().stream()
				.filter(t -> t.getExpiryDate().isBefore(now)).toList();
		blacklistedTokenRepository.deleteAll(expired);
	}
}
