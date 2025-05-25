package com.librarysystem.email;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.librarysystem.dto.ChangePasswordRequest;
import com.librarysystem.dto.ForgotPasswordRequest;
import com.librarysystem.dto.ResetPasswordRequest;
import com.librarysystem.model.User;
import com.librarysystem.otp.StroageOtp;
import com.librarysystem.otp.StroageOtp.OtpDetails;
import com.librarysystem.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private StroageOtp stroageOtp;

	// Send OTP to email
	public void sendOtpForReset(ForgotPasswordRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));

		String otp = generateOtp();
		stroageOtp.storeOtp(user.getEmail(), otp); // storing in StroageOtp class

		String token = UUID.randomUUID().toString();
		user.setResetToken(token);
		user.setTokenExpiry(LocalDateTime.now().plusMinutes(5));
		userRepository.save(user);

		String resetLink = "http://localhost:8080/reset-password?token=" + token;

		emailService.sendOtpEmail(user.getEmail(), user.getName(), otp, resetLink);
	}

	// Reset password via OTP
	public void resetPassword(ResetPasswordRequest request, String token) {
		OtpDetails otpData = stroageOtp.getOtpDetails(request.getEmail());

		if (otpData == null || !otpData.getOtp().equals(request.getOtp())) {
			throw new RuntimeException("Invalid OTP");
		}

		long currentTimeMillis = System.currentTimeMillis();
		if (otpData.getTimestamp() + (5 * 60 * 1000) < currentTimeMillis) {
			stroageOtp.removeOtp(request.getEmail());
			throw new RuntimeException("OTP expired");
		}

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (!token.equals(user.getResetToken())) {
			throw new RuntimeException("Invalid token");
		}
		if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Token expired");
		}

		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userRepository.save(user);

		user.setResetToken(null);
		user.setTokenExpiry(null);

		stroageOtp.removeOtp(request.getEmail()); // clear OTP after success
	}

	// Change password via oldPassword
	public User changePassword(ChangePasswordRequest changePasswordRequest) {
		User user = userRepository.findByEmail(changePasswordRequest.getEmail()).orElseThrow(
				() -> new RuntimeException("User not found with email: " + changePasswordRequest.getEmail()));

		if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
			throw new RuntimeException("Old password is incorrect.");
		}

		user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
		return userRepository.save(user);
	}

	// Utility for OTP generation
	private String generateOtp() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp); // Convert int to string
	}

}
