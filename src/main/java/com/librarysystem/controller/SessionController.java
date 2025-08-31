package com.librarysystem.controller;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.librarysystem.exception.UserNotFoundException;
import com.librarysystem.exception.handleInvalidRequest;
import com.librarysystem.model.ActiveAccessToken;
import com.librarysystem.model.Role;
import com.librarysystem.model.User;
import com.librarysystem.repository.ActiveAccessTokenRepository;
import com.librarysystem.repository.UserRepository;
import com.librarysystem.security.service.BlacklistedTokenService;
import com.librarysystem.security.service.CustomUserDetails;
import com.librarysystem.security.service.CustomUserDetailsService;
import com.librarysystem.security.service.RefreshTokenService;
import com.librarysystem.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ActiveAccessTokenRepository activeAccessTokenRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private BlacklistedTokenService blacklistedTokenService;

    // Logout current device (invalidate current refresh token)
    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> logoutCurrentDevice(@RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {

        String userIdStr = request.get("userId");
        if (userIdStr == null || userIdStr.isEmpty()) {
            throw new handleInvalidRequest("Missing 'userId' in request.");
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new handleInvalidRequest("Invalid userId format.");
        }

        // 1. Get userId from request
        userId = Long.parseLong(request.get("userIdStr"));// need to work on it
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        // 2. Delete refresh token
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new handleInvalidRequest("Missing refreshToken in request.");
        }
        refreshTokenService.deleteByToken(refreshToken);

        // 3. Blacklist current access token
        String accessToken = jwtUtil.getJwtFromRequest(httpRequest);
        Instant expiry = jwtUtil.getExpiryFromToken(accessToken);
        blacklistedTokenService.blacklistToken(accessToken, expiry);

        // 4. Delete current access token from active tokens (not all)
        ActiveAccessToken currentToken = activeAccessTokenRepository.findByTokenAndUser(accessToken, user);
        if (currentToken != null) {
            activeAccessTokenRepository.delete(currentToken);
        }

        return ResponseEntity.ok("Logged out from current device.");

    }

    // Logout from all devices
    @PostMapping("/logoutAllDevices")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> logoutAllDevices(@RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = Long.parseLong(request.get("userId"));

            // 1. Fetch the user need to work on it
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

            // 2. Delete all refresh tokens of this user
            refreshTokenService.deleteByUser(user);

            // 3. Get all active access tokens of this user
            List<ActiveAccessToken> activeTokens = activeAccessTokenRepository.findByUser(user);

            // 4. Blacklist all tokens
            for (ActiveAccessToken token : activeTokens) {
                blacklistedTokenService.blacklistToken(token.getToken(), token.getExpiryDate());
            }

            // 5. delete from ActiveAccessToken table after blacklisting
            activeAccessTokenRepository.deleteAll(activeTokens);

            return ResponseEntity.ok("Logged out from all devices successfully.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (!refreshTokenService.isValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        String userEmail = refreshTokenService.getUserEmailFromToken(refreshToken);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
        CustomUserDetails customDetails = (CustomUserDetails) userDetails;
        Role userRole = customDetails.getUser().getRole();
        final String newAccessToken = jwtUtil.generateToken(userEmail, userRole);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

}
