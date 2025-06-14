package com.librarysystem.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name="blacklisted_Token")
public class BlacklistedToken {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blacklistedToken_id;

    @Column(unique = true, nullable = false)
    private String blacklistedToken;

    @Column(nullable = false)
    private Instant expiryDate;

	public Long getBlacklistedToken_id() {
		return blacklistedToken_id;
	}

	public void setBlacklistedToken_id(Long blacklistedToken_id) {
		this.blacklistedToken_id = blacklistedToken_id;
	}

	public String getBlacklistedToken() {
		return blacklistedToken;
	}

	public void setBlacklistedToken(String blacklistedToken) {
		this.blacklistedToken = blacklistedToken;
	}

	public Instant getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Instant expiryDate) {
		this.expiryDate = expiryDate;
	}

	@Override
	public String toString() {
		return "BlacklistedToken [blacklistedToken_id=" + blacklistedToken_id + ", blacklistedToken=" + blacklistedToken
				+ ", expiryDate=" + expiryDate + "]";
	}
    
    

}
