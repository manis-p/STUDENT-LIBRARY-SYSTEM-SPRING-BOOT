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
import org.springframework.validation.annotation.Validated;
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
import com.librarysystem.exception.DatabaseOperationException;
import com.librarysystem.exception.InvalidUserDetailsTypeException;
import com.librarysystem.exception.TokenGenerationException;
import com.librarysystem.exception.UserNotFoundException;
import com.librarysystem.model.ActiveAccessToken;
import com.librarysystem.model.RefreshToken;
import com.librarysystem.model.Role;
import com.librarysystem.model.User;
import com.librarysystem.model.UserLoginActivity;
import com.librarysystem.repository.ActiveAccessTokenRepository;
import com.librarysystem.repository.UserLoginActivityRepository;
import com.librarysystem.repository.UserRepository;
import com.librarysystem.security.service.CustomUserDetails;
import com.librarysystem.security.service.CustomUserDetailsService;
import com.librarysystem.security.service.RefreshTokenService;
import com.librarysystem.service.UserServiceImpl;
import com.librarysystem.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/user")
@Validated
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
    private UserLoginActivityRepository userLoginActivityRepository;
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> userSignUp(@Valid @RequestBody SignupRequestDto signupRequestDto) {

        User savedUser = userServiceImpl.registerUser(signupRequestDto);
        // System.out.println("Received request: " + signupRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);

    }

    // this URL for the the admin panel to signup
    @PostMapping("/signup/admin")
    public ResponseEntity<?> adminSignUp(@Valid @RequestBody SignupRequestDto signupRequestDto) {

        User savedAdmin = userServiceImpl.registerAdmin(signupRequestDto);
        System.out.println("Received request: " + signupRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdmin);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {

        // Check if user exists and is not deleted
        User user = userRepository.findByEmailAndIsDeletedFalse(authRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("user not found."));

        // Authenticate credentials
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        userService.sendOtpForLogin(authRequest.getEmail());

        return ResponseEntity.ok("OTP sent to your email");

    }

    @GetMapping("/user/profile")
     @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getProfile(@RequestParam @NotBlank @Email String email) {

        User user = userServiceImpl.getUserProfile(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/user/update")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> updateUser(@Valid @RequestParam String email, @RequestBody UpdateProfileRequestDto dto) {

        User user = userServiceImpl.updateUserProfile(email, dto);
        return ResponseEntity.ok(user);
    }

    // user delete
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") @Min(1) Long userId) {

        userServiceImpl.deleteUser(userId);
        return ResponseEntity.ok("user deleted sucesfuy");

    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest otpRequest, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        // Validate OTP
        userService.verifyLoginOtp(otpRequest.getEmail(), otpRequest.getOtp());

        // Load user details
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(otpRequest.getEmail());
        if (!(userDetails instanceof CustomUserDetails)) {
            throw new InvalidUserDetailsTypeException("UserDetails is not of type CustomUserDetails.");
        }
        CustomUserDetails customDetails = (CustomUserDetails) userDetails;
        User user = customDetails.getUser();
        Role userRole = user.getRole();

        // Generate JWT and refresh token
        String jwt;
        try {
            jwt = jwtUtil.generateToken(userDetails.getUsername(), userRole);
        } catch (Exception e) {
            throw new TokenGenerationException("Unable to generate JWT token");
        }
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        // Save active token
        try {
            ActiveAccessToken activeToken = new ActiveAccessToken();
            activeToken.setUser(user);
            activeToken.setToken(jwt);
            activeToken.setExpiryDate(jwtUtil.getExpiryFromToken(jwt));

            activeAccessTokenRepository.save(activeToken);

        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to save active access token");
        }

        // Update login time
        try {
            // this for the qick acces
            user.setLastLogin(LocalDateTime.now());
            user.setLastLoginIp(ipAddress);
            user.setLastLoginDevice(userAgent);
            userRepository.save(user);

            // this is for the login history
            UserLoginActivity UserLoginActivity = new UserLoginActivity();
            UserLoginActivity.setUser(user);
            UserLoginActivity.setLoginTime(LocalDateTime.now());
            UserLoginActivity.setIpAddress(ipAddress);
            UserLoginActivity.setDevice(userAgent);
            userLoginActivityRepository.save(UserLoginActivity);
            // UserLoginActivity.setLocation("Unknown");

        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to save Update login time ");

        }

        // Return tokens
        AuthResponseWithRefreshToken response = new AuthResponseWithRefreshToken(jwt, refreshToken.getToken());
        return ResponseEntity.ok(response);

    }

}
