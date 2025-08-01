package com.librarysystem.unit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.librarysystem.email.EmailService;

import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MimeMessageHelper mimeMessageHelper;

    @Test
    void testSendOtpEmail_LoginPurpose() throws Exception {
        String email = "test@example.com";
        String userName = "Test User";
        String otp = "123456";
        String resetLink = null;
        String purpose = "LOGIN";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendOtpEmail(email, userName, otp, resetLink, purpose);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendOtpEmail_PasswordResetPurpose() throws Exception {
        String email = "test@example.com";
        String userName = "Test User";
        String otp = null;
        String resetLink = "http://localhost/reset?token=abc";
        String purpose = "RESET";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendOtpEmail(email, userName, otp, resetLink, purpose);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

}