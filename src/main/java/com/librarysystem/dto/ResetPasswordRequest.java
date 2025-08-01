package com.librarysystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank(message = "Email is required")
	@Email(message = "Email should be valid")
	private String email;
	@NotBlank(message = "OTP is required")
	@Size(min = 6, max = 6, message = "OTP must be exactly 6 characters")
	@Pattern(regexp = "^[0-9]+$", message = "OTP must be numeric")
    private String otp;
	@NotBlank(message = "Password is required")
	@Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,20}$", 
			 message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")	
    private String newPassword;
	public String getEmail() {
		return email;
		// iska muje vaiadate  ka karna hai controer main 
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
    
    
}
