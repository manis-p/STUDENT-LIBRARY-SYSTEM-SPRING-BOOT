package com.librarysystem.dto;

import com.librarysystem.model.Role;

//@Data
public class SignupRequestDto {
	private String name;
	private String email;
	private String password;
	private String phone;
	 private Role role;
	
	public SignupRequestDto(String name, String email, String password, String phone, Role role) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.role = role;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
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
	
	
	
}
