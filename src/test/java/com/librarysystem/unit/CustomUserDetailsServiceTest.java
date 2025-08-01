package com.librarysystem.unit;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.librarysystem.model.User;
import com.librarysystem.repository.UserRepository;
import com.librarysystem.security.service.CustomUserDetails;
import com.librarysystem.security.service.CustomUserDetailsService;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;
    
    @Test
    void testLoadUserByUsername_Success() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setDeleted(false);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        UserDetails result = customUserDetailsService.loadUserByUsername(email);

        assertNotNull(result);
        assertTrue(result instanceof CustomUserDetails);
        assertEquals(email, ((CustomUserDetails) result).getUser().getEmail());
        verify(userRepository).findByEmail(email);
    }
}