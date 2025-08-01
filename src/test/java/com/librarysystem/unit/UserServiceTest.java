package com.librarysystem.unit;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.librarysystem.dto.ChangePasswordRequest;
import com.librarysystem.dto.ForgotPasswordRequest;
import com.librarysystem.dto.ResetPasswordRequest;
import com.librarysystem.email.EmailService;
import com.librarysystem.email.UserService;
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

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Spy
    @InjectMocks
    private UserService userService;
    @Mock
    private StroageOtp stroageOtp;
    @Mock
    private EmailService emailService;
    // @InjectMocks
    // private UserServiceImpl userService;
    // @Spy
    // private final UserService realUserService = new UserService(); // for
    // generateOtp()

    @Test
    void testSendOtpForReset() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@example.com");

        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setName("Test User");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        // Act
        userService.sendOtpForReset(request);

        // Assert
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(stroageOtp, times(1)).storeOtp(eq("test@example.com"), anyString());
        verify(userRepository, times(1)).save(mockUser);
        verify(emailService, times(1)).sendOtpEmail(
                eq("test@example.com"),
                eq("Test User"),
                anyString(),
                contains("http://localhost:8080/reset-password?token=" + mockUser.getResetToken()),
                isNull());
    }

    @Test
    void testSendOtpForReset_UserNotFound_ShouldThrowException() {

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("notfound@example.com");

        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.sendOtpForReset(request);
        });

        verify(userRepository, times(1)).findByEmail("notfound@example.com");
        verifyNoMoreInteractions(stroageOtp, userRepository, emailService);
    }

    @Test
    void testSendOtpForReset_EmailSendFails_ShouldThrowException() {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@example.com");

        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setName("Test User");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        doThrow(new RuntimeException("Mail failure"))
                .when(emailService)
                .sendOtpEmail(anyString(), anyString(), anyString(), anyString(), isNull());

        // Act + Assert
        assertThrows(EmailSendFailedException.class, () -> {
            userService.sendOtpForReset(request);
        });

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(stroageOtp, times(1)).storeOtp(eq("test@example.com"), anyString());
        verify(emailService, times(1)).sendOtpEmail(anyString(), anyString(), anyString(), anyString(), isNull());
    }

    @Test
    void testResetPassword() {
        String email = "test@example.com";
        String otp = "123456";
        String token = "valid-token";
        String encodedPassword = "encodedPass";

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail(email);
        request.setOtp(otp);
        request.setNewPassword("newPassword");

        OtpDetails otpDetails = new OtpDetails(otp, System.currentTimeMillis());

        User user = new User();
        user.setEmail(email);
        user.setResetToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(5));

        when(stroageOtp.getOtpDetails(email)).thenReturn(otpDetails);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn(encodedPassword);

        userService.resetPassword(request, token);

        assertEquals(encodedPassword, user.getPassword());
        assertNull(user.getResetToken());
        assertNull(user.getTokenExpiry());

        verify(userRepository).save(user);
        verify(stroageOtp).removeOtp(email);
    }

    @Test
    void testResetPassword_InvalidOtp() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("test@example.com");
        request.setOtp("wrongOtp");

        OtpDetails otpDetails = new OtpDetails("correctOtp", System.currentTimeMillis());
        when(stroageOtp.getOtpDetails(request.getEmail())).thenReturn(otpDetails);

        assertThrows(InvalidOtpException.class, () -> {
            userService.resetPassword(request, "someToken");
        });
    }

    @Test
    void testResetPassword_ExpiredOtp() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("test@example.com");
        request.setOtp("123456");

        // Expired OTP
        OtpDetails otpDetails = new OtpDetails("123456", System.currentTimeMillis() - (6 * 60 * 1000));
        when(stroageOtp.getOtpDetails(request.getEmail())).thenReturn(otpDetails);

        assertThrows(OtpExpiredException.class, () -> {
            userService.resetPassword(request, "token");
        });

        verify(stroageOtp).removeOtp(request.getEmail());
    }

    @Test
    void testResetPassword_InvalidToken() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("test@example.com");
        request.setOtp("123456");

        OtpDetails otpDetails = new OtpDetails("123456", System.currentTimeMillis());
        User user = new User();
        user.setEmail("test@example.com");
        user.setResetToken("valid-token");
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(5));

        when(stroageOtp.getOtpDetails(request.getEmail())).thenReturn(otpDetails);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        assertThrows(nvalidTokenException.class, () -> {
            userService.resetPassword(request, "wrong-token");
        });
    }

    @Test
    void testResetPassword_TokenExpired() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("test@example.com");
        request.setOtp("123456");

        OtpDetails otpDetails = new OtpDetails("123456", System.currentTimeMillis());
        User user = new User();
        user.setEmail("test@example.com");
        user.setResetToken("token");
        user.setTokenExpiry(LocalDateTime.now().minusMinutes(1));

        when(stroageOtp.getOtpDetails(request.getEmail())).thenReturn(otpDetails);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        assertThrows(TokenExpiredException.class, () -> {
            userService.resetPassword(request, "token");
        });
    }

    @Test
    void testChangePassword() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setEmail("test@example.com");
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedOldPass");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "encodedOldPass")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.changePassword(request);

        // Assert
        assertEquals("encodedNewPass", result.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void testChangePassword_UserNotFound() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setEmail("notfound@example.com");
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.changePassword(request));
    }

    @Test
    void testChangePassword_IncorrectOldPassword() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setEmail("test@example.com");
        request.setOldPassword("wrongOldPass");
        request.setNewPassword("newPass");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedOldPass");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOldPass", "encodedOldPass")).thenReturn(false);

        // Act & Assert
        assertThrows(IncorrectOldPasswordException.class, () -> userService.changePassword(request));
    }

    @Test
    void testSendOtpForLogin() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setName("Test User");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        String generatedOtp = "123456";
        doReturn(generatedOtp).when(userService).generateOtp();

        // Act
        userService.sendOtpForLogin(email);

        // Assert
        verify(userRepository, times(1)).findByEmail(email);
        verify(stroageOtp, times(1)).storeOtp(email, generatedOtp);
        verify(emailService, times(1)).sendOtpEmail(email, "Test User", generatedOtp, null, "LOGIN");
    }

    @Test
    void testSendOtpForLogin_UserNotFound_ThrowsException() {
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.sendOtpForLogin(email);
        });

        verify(emailService, never()).sendOtpEmail(any(), any(), any(), any(), any());
    }

    @Test
    void testVerifyLoginOtp_Success() {
        // Arrange
        String email = "test@example.com";
        String otp = "123456";
        long currentTime = System.currentTimeMillis();

        User user = new User();
        user.setEmail(email);

        OtpDetails otpDetails = new OtpDetails(otp, currentTime);

        when(userRepository.findByEmailAndIsDeletedFalse(email)).thenReturn(Optional.of(user));
        when(stroageOtp.getOtpDetails(email)).thenReturn(otpDetails);

        // Act & Assert
        assertDoesNotThrow(() -> userService.verifyLoginOtp(email, otp));

        verify(stroageOtp, times(1)).removeOtp(email);
    }

    @Test
    void testVerifyLoginOtp_UserNotFound_ThrowsException() {
        String email = "invalid@example.com";
        String otp = "123456";

        when(userRepository.findByEmailAndIsDeletedFalse(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.verifyLoginOtp(email, otp);
        });

        verify(stroageOtp, never()).getOtpDetails(anyString());
    }

    @Test
    void testVerifyLoginOtp_InvalidOtp_ThrowsException() {
        String email = "test@example.com";
        String providedOtp = "999999";

        User user = new User();
        user.setEmail(email);

        OtpDetails otpDetails = new OtpDetails("123456", System.currentTimeMillis());

        when(userRepository.findByEmailAndIsDeletedFalse(email)).thenReturn(Optional.of(user));
        when(stroageOtp.getOtpDetails(email)).thenReturn(otpDetails);

        assertThrows(InvalidOtpException.class, () -> {
            userService.verifyLoginOtp(email, providedOtp);
        });

        verify(stroageOtp, never()).removeOtp(email);
    }

    @Test
    void testVerifyLoginOtp_ExpiredOtp_ThrowsException() {
        String email = "test@example.com";
        String otp = "123456";

        User user = new User();
        user.setEmail(email);

        // OTP 6 minutes old (360000 ms)
        long oldTimestamp = System.currentTimeMillis() - (6 * 60 * 1000);
        OtpDetails otpDetails = new OtpDetails(otp, oldTimestamp);

        when(userRepository.findByEmailAndIsDeletedFalse(email)).thenReturn(Optional.of(user));
        when(stroageOtp.getOtpDetails(email)).thenReturn(otpDetails);

        assertThrows(OtpExpiredException.class, () -> {
            userService.verifyLoginOtp(email, otp);
        });

        verify(stroageOtp, times(1)).removeOtp(email);
    }

}