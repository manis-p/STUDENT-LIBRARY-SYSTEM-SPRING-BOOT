package com.librarysystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.librarysystem.model.ActiveAccessToken;
import com.librarysystem.model.User;

public interface ActiveAccessTokenRepository extends JpaRepository<ActiveAccessToken, Long> {

	List<ActiveAccessToken> findByUser(User user);

	public ActiveAccessToken findByTokenAndUser(String token, User user);
}
