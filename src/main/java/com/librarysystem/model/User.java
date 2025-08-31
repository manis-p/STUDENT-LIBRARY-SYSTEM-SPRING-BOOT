package com.librarysystem.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Column(unique = true, nullable = false)
	private String email;

	private String password;
	private String phone;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, name = "role")
	private Role role;

	@Column(updatable = false)
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private String resetToken;
	private LocalDateTime tokenExpiry;

	private boolean isDeleted = false;

	@Column(name = "last_login")
	private LocalDateTime lastLogin;

	private String lastLoginIp;
	private String lastLoginDevice;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private final List<UserLoginActivity> loginActivities = new ArrayList<>();

	public User() {
	}

	public User(String name, String email, String password, String phone, Role role,
			LocalDateTime createdAt, LocalDateTime updatedAt, String resetToken,
			LocalDateTime tokenExpiry, boolean isDeleted, LocalDateTime lastLogin,
			String lastLoginIp, String lastLoginDevice) {
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

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
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

	public String getResetToken() {
		return resetToken;
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

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean deleted) {
		isDeleted = deleted;
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

	public List<UserLoginActivity> getLoginActivities() {
		return loginActivities;
	}

	public void setLoginActivities(List<UserLoginActivity> loginActivities) {
		this.loginActivities.clear();
		if (loginActivities != null) {
			for (UserLoginActivity activity : loginActivities) {
				activity.setUser(this); 
				this.loginActivities.add(activity);
			}
		}
	}

	public void addLoginActivity(UserLoginActivity activity) {
		loginActivities.add(activity);
		activity.setUser(this);
	}

	public void removeLoginActivity(UserLoginActivity activity) {
		loginActivities.remove(activity);
		activity.setUser(null);
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", name='" + name + '\'' +
				", email='" + email + '\'' +
				", phone='" + phone + '\'' +
				", role=" + role +
				", createdAt=" + createdAt +
				", updatedAt=" + updatedAt +
				", isDeleted=" + isDeleted +
				", lastLogin=" + lastLogin +
				", lastLoginIp='" + lastLoginIp + '\'' +
				", lastLoginDevice='" + lastLoginDevice + '\'' +
				'}';
	}

}
