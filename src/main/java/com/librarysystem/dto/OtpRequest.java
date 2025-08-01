package com.librarysystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class OtpRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be exactly 6 numeric characters")
    @Size(min = 6, max = 6, message = "OTP must be exactly 6 characters")
    private String otp;

    public String getEmail() {
        return email;
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

    public OtpRequest(String email, String otp) {
        super();
        this.email = email;
        this.otp = otp;
    }

    public OtpRequest() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "OtpRequest [email=" + email + ", otp=" + otp + "]";
    }

}
