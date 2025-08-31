package com.librarysystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    // By ut old password and new password
    // Change password (authenticated user)
    @PostMapping("/change-password")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        User result = userService.changePassword(request);
        return ResponseEntity.ok(result);
    }

    // Forgot password (send OTP email)
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.sendOtpForReset(request);
        return ResponseEntity.ok("OTP sent to your email.");
    }

    // Reset password (verify OTP + set new password)
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequest request,
            @RequestParam("token") String token) {
        userService.resetPassword(request, token);
        return ResponseEntity.ok("Password reset successfully.");

    }
}
