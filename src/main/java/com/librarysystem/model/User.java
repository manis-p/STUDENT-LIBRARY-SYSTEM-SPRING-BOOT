package com.librarysystem.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
	private Long id;
	private String name;
	@Column(unique = true, nullable = false)
	private String email;
	private String password;
	private String phone;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, name = "role")
	private Role role;
	@CreationTimestamp
	private LocalDateTime createdAt;
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	private String resetToken;
	private LocalDateTime tokenExpiry;
	private boolean isDeleted = false;
	@Column(name = "last_login")
	private LocalDateTime lastLogin;
	private String lastLoginIp;
	private String lastLoginDevice;

	public User(Long id, String name, String email, String password, String phone, Role role, LocalDateTime createdAt,
			LocalDateTime updatedAt, String resetToken, LocalDateTime tokenExpiry, boolean isDeleted,
			LocalDateTime lastLogin, String lastLoginIp, String lastLoginDevice) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.role = role;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.resetToken = resetToken;
		this.tokenExpiry = tokenExpiry;
		this.isDeleted = isDeleted;
		this.lastLogin = lastLogin;
		this.lastLoginIp = lastLoginIp;
		this.lastLoginDevice = lastLoginDevice;
	}

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getLastLoginIp() {
		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	public String getLastLoginDevice() {
		return lastLoginDevice;
	}

	public void setLastLoginDevice(String lastLoginDevice) {
		this.lastLoginDevice = lastLoginDevice;
	}

	public String getResetToken() {
		return resetToken;
	}

	public User(Long id, String name, String email, String password, String phone, Role role, LocalDateTime createdAt,
			LocalDateTime updatedAt, String resetToken, LocalDateTime tokenExpiry, boolean isDeleted) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.role = role;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.resetToken = resetToken;
		this.tokenExpiry = tokenExpiry;
		this.isDeleted = isDeleted;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public LocalDateTime getTokenExpiry() {
		return tokenExpiry;
	}

	public void setTokenExpiry(LocalDateTime tokenExpiry) {
		this.tokenExpiry = tokenExpiry;
	}

	public User() {

	}

	public User(Long id, String name, String email, String password, String phone, Role role, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.role = role;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", phone=" + phone
				+ ", role=" + role + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", resetToken="
				+ resetToken + ", tokenExpiry=" + tokenExpiry + ", isDeleted=" + isDeleted + "]";
	}

}
