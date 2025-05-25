package com.librarysystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.librarysystem.dto.ChangePasswordRequest;
import com.librarysystem.dto.ForgotPasswordRequest;
import com.librarysystem.dto.ResetPasswordRequest;
import com.librarysystem.email.UserService;
import com.librarysystem.model.User;
@RestController
@RequestMapping("/api/user")
public class AuthController {

	@Autowired
	private UserService userService;

	// Change password (authenticated user)
	@PostMapping("/change-password")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
		User result = userService.changePassword(request);
		return ResponseEntity.ok(result);
	}

	// Forgot password (send OTP email)
	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
		try {
			userService.sendOtpForReset(request);
			return ResponseEntity.ok("OTP sent to your email.");
		} catch (Exception e) {
			 e.printStackTrace(); 
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}

	}

	// Reset password (verify OTP + set new password)
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request,@RequestParam("token") String token) {
		userService.resetPassword(request,token);
		return ResponseEntity.ok("Password reset successfully.");
	}

}
