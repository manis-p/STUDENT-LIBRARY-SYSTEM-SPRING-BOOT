package com.librarysystem.unit;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.librarysystem.model.RefreshToken;
import com.librarysystem.model.User;
import com.librarysystem.repository.RefreshTokenRepository;
import com.librarysystem.repository.UserRepository;
import com.librarysystem.security.service.RefreshTokenService;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceRefresherTokenTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    // Test for createRefreshToken method
    @Test
    void testCreateRefreshToken() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken token = refreshTokenService.createRefreshToken(userId);
        assertNotNull(token);
        assertEquals(user, token.getUser());
        assertTrue(token.getExpiryDate().isAfter(Instant.now()));
        verify(refreshTokenRepository).save(token);
    }

    // Test for isValid method
    @Test
    void testIsValid() {
        String tokenString = "valid-token";
        RefreshToken token = new RefreshToken();
        token.setToken(tokenString);
        token.setExpiryDate(Instant.now().plusSeconds(3600));

        when(refreshTokenRepository.findByToken(tokenString)).thenReturn(Optional.of(token));

        boolean isValid = refreshTokenService.isValid(tokenString);
        assertTrue(isValid);
        verify(refreshTokenRepository).findByToken(tokenString);
    }

    // Test for deleteByToken method
    @Test
    void testDeleteByToken() {
        String tokenString = "delete-token";

        refreshTokenService.deleteByToken(tokenString);

        verify(refreshTokenRepository).deleteByToken(tokenString);
    }

    // Test for findByToken method
    @Test
    void testFindByToken() {
        String tokenString = "find-token";
        RefreshToken token = new RefreshToken();
        token.setToken(tokenString);
        when(refreshTokenRepository.findByToken(tokenString)).thenReturn(Optional.of(token));

        Optional<RefreshToken> foundToken = refreshTokenService.findByToken(tokenString);
        assertTrue(foundToken.isPresent());
        assertEquals(tokenString, foundToken.get().getToken());
        verify(refreshTokenRepository).findByToken(tokenString);
    }

    @Test
    void testDeleteByUser() {
        User user = new User();

        when(refreshTokenRepository.deleteByUser(user)).thenReturn(1);

        int result = refreshTokenService.deleteByUser(user);

        assertEquals(1, result);
        verify(refreshTokenRepository).deleteByUser(user);

    }

    @Test
    void testFindAllByUser() {
        User user = new User();

        List<RefreshToken> mockTokens = Arrays.asList(new RefreshToken(), new RefreshToken());

        when(refreshTokenRepository.findAllByUser(user)).thenReturn(mockTokens);

        List<RefreshToken> result = refreshTokenService.findAllByUser(user);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(refreshTokenRepository).findAllByUser(user);
    }

    @Test
    void testGetUserEmailFromToken() {
        String token = "sample-token";
        String email = "user@example.com";

        User user = new User();
        user.setEmail(email);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));
        String resultEmail = refreshTokenService.getUserEmailFromToken(token);
        assertEquals(email, resultEmail);
        verify(refreshTokenRepository).findByToken(token);
    }

    @Test
    void testIsTokenExpired() {
        RefreshToken token = new RefreshToken();
        Instant expiryDate = Instant.now().minusSeconds(3600);
        token.setExpiryDate(expiryDate);

        boolean isExpired = refreshTokenService.isTokenExpired(token);
        assertTrue(isExpired);
    }

}