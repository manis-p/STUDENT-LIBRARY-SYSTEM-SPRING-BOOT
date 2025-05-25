package com.librarysystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.librarysystem.dto.AuthRequest;
import com.librarysystem.dto.AuthResponse;
import com.librarysystem.dto.SignupRequestDto;
import com.librarysystem.dto.UpdateProfileRequestDto;
import com.librarysystem.model.Role;
import com.librarysystem.model.User;
import com.librarysystem.security.service.CustomUserDetails;
import com.librarysystem.security.service.CustomUserDetailsService;
import com.librarysystem.service.UserServiceImpl;
import com.librarysystem.util.JwtUtil;

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
		System.out.println(" heloo manish");

		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
		} catch (BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
		}catch (Exception e) {
		    e.printStackTrace(); 
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong: " + e.getMessage());
		}
		System.out.println(" heloo manish2");
		 System.out.println(authRequest.getEmail() + " "+ authRequest.getPassword());
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(authRequest.getEmail());
	    CustomUserDetails customDetails = (CustomUserDetails) userDetails;
	    Role userRole = customDetails.getUser().getRole();
	    System.out.println("here  i am manish jha ");
		final String jwt = jwtUtil.generateToken(userDetails.getUsername(), userRole);
		System.out.println(jwt);
		return ResponseEntity.ok(new AuthResponse(jwt));
	}

	@GetMapping("/profile")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
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
}
