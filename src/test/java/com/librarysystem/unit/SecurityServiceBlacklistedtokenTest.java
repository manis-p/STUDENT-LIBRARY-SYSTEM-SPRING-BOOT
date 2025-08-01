//  in this class i ma going  to test my sercurity service class   method which is more important for security
package com.librarysystem.unit;

import java.time.Instant;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.librarysystem.model.BlacklistedToken;
import com.librarysystem.repository.BlacklistedTokenRepository;
import com.librarysystem.security.service.BlacklistedTokenService;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceBlacklistedtokenTest {

    @Mock
    BlacklistedTokenRepository blacklistedTokenRepository;

    @InjectMocks
    BlacklistedTokenService blacklistedTokenService;

    // first method test
    @Test
    void SecurityServiceBlacklistedtoken() {
        String token = "abc.jwt.token";
        Instant expiry = Instant.now().plusSeconds(3600);
        blacklistedTokenService.blacklistToken(token, expiry);
        verify(blacklistedTokenRepository, times(1))
                .save(argThat(savedToken -> savedToken.getBlacklistedToken().equals(token) &&
                        savedToken.getExpiryDate().equals(expiry)));
    }

    @Test
    void testIsTokenBlacklisted() {
        String token = "abc.jwt.token";
        when(blacklistedTokenRepository.existsByBlacklistedToken(token)).thenReturn(true);
        boolean result = blacklistedTokenService.isTokenBlacklisted(token);
        assertTrue(result);
        verify(blacklistedTokenRepository).existsByBlacklistedToken(token);
    }

    @Test
    // 3rd method check .
    void testRemoveExpiredTokens() {
        Instant now = Instant.now();

        BlacklistedToken expiredToken1 = new BlacklistedToken();
        expiredToken1.setBlacklistedToken("token1");
        expiredToken1.setExpiryDate(now.minusSeconds(60));

        BlacklistedToken expiredToken2 = new BlacklistedToken();
        expiredToken2.setBlacklistedToken("token2");
        expiredToken2.setExpiryDate(now.minusSeconds(120));

        BlacklistedToken validToken = new BlacklistedToken();
        validToken.setBlacklistedToken("token3");
        validToken.setExpiryDate(now.plusSeconds(600));

        List<BlacklistedToken> allTokens = List.of(expiredToken1, expiredToken2, validToken);

        when(blacklistedTokenRepository.findAll()).thenReturn(allTokens);

        blacklistedTokenService.removeExpiredTokens();

        // Only the expired tokens should be deleted
        verify(blacklistedTokenRepository).deleteAll(argThat(tokens ->
                StreamSupport.stream(tokens.spliterator(), false).anyMatch(t -> t.equals(expiredToken1)) &&
                StreamSupport.stream(tokens.spliterator(), false).anyMatch(t -> t.equals(expiredToken2))
        ));
    }

}