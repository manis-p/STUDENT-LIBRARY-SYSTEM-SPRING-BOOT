package com.librarysystem.unit;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.librarysystem.dto.SignupRequestDto;
import com.librarysystem.dto.UpdateProfileRequestDto;
import com.librarysystem.model.Role;
import com.librarysystem.model.User;
import com.librarysystem.repository.UserRepository;
import com.librarysystem.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    // Test 1: checkIfEmailExists()
    @Test
    void testCheckIfEmailExists_ReturnsTrue() {
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean result = userService.checkIfEmailExists(email);

        assertTrue(result);
        verify(userRepository).existsByEmail(email);
    }

    // Test 2: registerUser()
    @Test
    void testRegisterUser_Success() {
        SignupRequestDto dto = new SignupRequestDto(
                "John",
                "john@example.com",
                "1234567890",
                "password",
                Role.ROLE_USER);

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName(dto.getName());
        savedUser.setEmail(dto.getEmail());
        savedUser.setPhone(dto.getPhone());
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(Role.ROLE_USER);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(dto);

        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    // Test 3: getUserProfile() - success case
    @Test
    void testGetUserProfile_Success() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.getUserProfile(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    // Test 4: updateUserProfile() - success case
    @Test
    void testUpdateUserProfile_Success() {
        String email = "update@example.com";

        UpdateProfileRequestDto dto = new UpdateProfileRequestDto();
        dto.setName("Updated Name");
        //dto.setPassword("newPass");
        dto.setPhone("9999999999");

        User existingUser = new User();
        existingUser.setEmail(email);
        existingUser.setDeleted(false);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User result = userService.updateUserProfile(email, dto);

        assertEquals("Updated Name", result.getName());
        assertEquals("newPass", result.getPassword());
        assertEquals("9999999999", result.getPhone());
    }

    // Test 5: deleteUser() - success case
    @Test
    void testDeleteUser_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setDeleted(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        assertTrue(user.isDeleted());
        verify(userRepository).save(user);
    }
}
