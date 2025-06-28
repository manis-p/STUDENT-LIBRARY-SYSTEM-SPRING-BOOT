package com.librarysystem.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.librarysystem.dto.AuthRequest;
import com.librarysystem.dto.AuthResponseWithRefreshToken;
import com.librarysystem.dto.OtpRequest;
import com.librarysystem.dto.SignupRequestDto;
import com.librarysystem.dto.UpdateProfileRequestDto;
import com.librarysystem.email.UserService;
import com.librarysystem.model.ActiveAccessToken;
import com.librarysystem.model.RefreshToken;
import com.librarysystem.model.Role;
import com.librarysystem.model.User;
import com.librarysystem.repository.ActiveAccessTokenRepository;
import com.librarysystem.repository.UserRepository;
import com.librarysystem.security.service.CustomUserDetails;
import com.librarysystem.security.service.CustomUserDetailsService;
import com.librarysystem.security.service.RefreshTokenService;
import com.librarysystem.service.UserServiceImpl;
import com.librarysystem.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class UserHandler {
	@Autowired
	public UserServiceImpl userServiceImpl;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private ActiveAccessTokenRepository activeAccessTokenRepository;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<?> userSignUp(@RequestBody SignupRequestDto signupRequestDto) {
		System.out.println("User signup API hit");
		try {
			User savedUser = userServiceImpl.registerUser(signupRequestDto);
			System.out.println("Received request: " + signupRequestDto);
			return ResponseEntity.ok(savedUser);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
		try {

			// Check if user exists and is not deleted
			User user = userRepository.findByEmailAndIsDeletedFalse(authRequest.getEmail())
					.orElseThrow(() -> new RuntimeException("Your profile is deleted. Please register again."));

			// Authenticate credentials
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

			// Send OTP
			userService.sendOtpForLogin(authRequest.getEmail());

			return ResponseEntity.ok("OTP sent to your email");

		} catch (BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");

		} catch (RuntimeException e) {
			if (e.getMessage().contains("deleted")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body("Your profile is deleted. Please register again.");
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Something went wrong: " + e.getMessage());
		}
	}

	@GetMapping("/profile")
	//@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> getProfile(@RequestParam String email) {
		try {
			User user = userServiceImpl.getUserProfile(email);
			return ResponseEntity.ok(user);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/update")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> updateUser(@RequestParam String email, @RequestBody UpdateProfileRequestDto dto) {
		try {
			User user = userServiceImpl.updateUserProfile(email, dto);
			return ResponseEntity.ok(user);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// user delete
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> deleteUser(@PathVariable("id") Long userId) {

		try {
			userServiceImpl.deleteUser(userId);
			return ResponseEntity.ok("user deleted sucesfuy");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());

		}

	}

	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest otpRequest, HttpServletRequest request) {
		try {

			String ipAddress = request.getRemoteAddr();
			String userAgent = request.getHeader("User-Agent");
			// Validate OTP
			boolean isValid = userService.verifyLoginOtp(otpRequest.getEmail(), otpRequest.getOtp());

			if (!isValid) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP");
			}

			// Load user details
			UserDetails userDetails = customUserDetailsService.loadUserByUsername(otpRequest.getEmail());
			CustomUserDetails customDetails = (CustomUserDetails) userDetails;
			User user = customDetails.getUser();
			Role userRole = user.getRole();

			// Generate JWT and refresh token
			String jwt = jwtUtil.generateToken(userDetails.getUsername(), userRole);
			RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

			// Save active token
			ActiveAccessToken activeToken = new ActiveAccessToken();
			activeToken.setUser(user);
			activeToken.setToken(jwt);
			activeToken.setExpiryDate(jwtUtil.getExpiryFromToken(jwt));
			activeAccessTokenRepository.save(activeToken);

			// Update login time
			user.setLastLogin(LocalDateTime.now());
			user.setLastLoginIp(ipAddress);
			user.setLastLoginDevice(userAgent);
			userRepository.save(user);

			// Return tokens
			AuthResponseWithRefreshToken response = new AuthResponseWithRefreshToken(jwt, refreshToken.getToken());
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Something went wrong: " + e.getMessage());
		}
	}

}
