package com.librarysystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.librarysystem.model.BlacklistedToken;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken,Long> {
	
	boolean existsByBlacklistedToken(String token);

}
