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
import com.librarysystem.exception.EmailSendFailedException;
import com.librarysystem.exception.IncorrectOldPasswordException;
import com.librarysystem.exception.InvalidOtpException;
import com.librarysystem.exception.OtpExpiredException;
import com.librarysystem.exception.TokenExpiredException;
import com.librarysystem.exception.UserNotFoundException;
import com.librarysystem.exception.nvalidTokenException;
import com.librarysystem.model.User;
import com.librarysystem.otp.StroageOtp;
import com.librarysystem.otp.StroageOtp.OtpDetails;
import com.librarysystem.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
    public EmailService emailService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private StroageOtp stroageOtp;


	// Reset password via OTP
	public void sendOtpForReset(ForgotPasswordRequest request) {
		User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
				() -> new UserNotFoundException("No user registered with this email: " + request.getEmail()));

		String otp = generateOtp();
		stroageOtp.storeOtp(user.getEmail(), otp); // storing in StroageOtp class

		String token = UUID.randomUUID().toString();
		user.setResetToken(token);
		user.setTokenExpiry(LocalDateTime.now().plusMinutes(5));
		userRepository.save(user);

		String resetLink = "http://localhost:8080/reset-password?token=" + token;

		try {
			emailService.sendOtpEmail(user.getEmail(), user.getName(), otp, resetLink, null);
		} catch (Exception e) {
			throw new EmailSendFailedException("Failed to send OTP email. Please try again.");
		}
	}

	// verify
	public void resetPassword(ResetPasswordRequest request, String token) {
		OtpDetails otpData = stroageOtp.getOtpDetails(request.getEmail());

		if (otpData == null || !otpData.getOtp().equals(request.getOtp())) {
			throw new InvalidOtpException("Invalid OTP");
		}

		long currentTimeMillis = System.currentTimeMillis();
		if (otpData.getTimestamp() + (5 * 60 * 1000) < currentTimeMillis) {
			stroageOtp.removeOtp(request.getEmail());
			throw new OtpExpiredException("OTP expired");
		}

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new UserNotFoundException("User not found")); // yeha bhi thik karna hsi

		if (!token.equals(user.getResetToken())) {
			throw new nvalidTokenException("Invalid token");
		}
		if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
			throw new TokenExpiredException("Token expired");
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
				() -> new UserNotFoundException("User not found with email: " + changePasswordRequest.getEmail()));

		if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
			throw new IncorrectOldPasswordException("Old password is incorrect.");
		}

		user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
		return userRepository.save(user);
	}

	// Utility for OTP generation
	//iska test nahi likha h
	public String generateOtp() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp); // Convert int to string
	}

	// this for send the Otp for login
	public void sendOtpForLogin(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

		String otp = generateOtp();
		stroageOtp.storeOtp(user.getEmail(), otp);

		emailService.sendOtpEmail(user.getEmail(), user.getName(), otp, null, "LOGIN");
	}

	public void verifyLoginOtp(String email, String otp) {

		User user = userRepository.findByEmailAndIsDeletedFalse(email)
				.orElseThrow(() -> new UserNotFoundException("user not found."));

		OtpDetails otpDetails = stroageOtp.getOtpDetails(user.getEmail());

		if (otpDetails == null || !otpDetails.getOtp().equals(otp)) {
			throw new InvalidOtpException("Invalid OTP"); 
		}

		if (System.currentTimeMillis() > otpDetails.getTimestamp() + 5 * 60 * 1000) {
			stroageOtp.removeOtp(user.getEmail());
			throw new OtpExpiredException("OTP expired");
		}

		stroageOtp.removeOtp(user.getEmail());

	}

}
