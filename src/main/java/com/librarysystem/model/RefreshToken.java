package com.librarysystem.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long tokenid;

	@Column(nullable = false, unique = true)
	private String token;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id",referencedColumnName = "id")
	private User user;

	private Instant expiryDate;

	public RefreshToken() {
		super();

	}

	public Long getTokenid() {
		return tokenid;
	}

	public void setTokenid(Long tokenid) {
		this.tokenid = tokenid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Instant getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Instant expiryDate) {
		this.expiryDate = expiryDate;
	}

	@Override
	public String toString() {
		return "RefreshToken [tokenid=" + tokenid + ", token=" + token + ", user=" + user + ", expiryDate=" + expiryDate
				+ "]";
	}
	

}
