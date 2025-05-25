package com.librarysystem.dto;

public class UpdateProfileRequestDto {

	private String name;
    private String phone;
    private String password;
    
    public UpdateProfileRequestDto() {
    	
    }

	public UpdateProfileRequestDto(String name, String phone, String password) {
		super();
		this.name = name;
		this.phone = phone;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "UpdateProfileRequestDto [name=" + name + ", phone=" + phone + ", password=" + password + "]";
	}
    
    
	
}
