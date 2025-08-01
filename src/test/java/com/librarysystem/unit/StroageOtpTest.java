package com.librarysystem.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.librarysystem.otp.StroageOtp;
import com.librarysystem.otp.StroageOtp.OtpDetails;

@ExtendWith(MockitoExtension.class)
public class StroageOtpTest {

    @InjectMocks
    StroageOtp stroageOtp;

    @Test
    void testStoreOtp_Success() {
        // Arrange
        String email = "test@example.com";
        String otp = "123456";

        // Act
        stroageOtp.storeOtp(email, otp);

        // Assert
        OtpDetails storedOtpDetails = stroageOtp.getOtpDetails(email);
        assertNotNull(storedOtpDetails, "OTP Details should not be null");
        assertEquals(otp, storedOtpDetails.getOtp(), "Stored OTP should match");
        assertTrue(System.currentTimeMillis() - storedOtpDetails.getTimestamp() < 1000,
                "Timestamp should be recent");
    }

}
