package com.librarysystem.otp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class StroageOtp {

    private Map<String, OtpDetails> otpCache = new ConcurrentHashMap<>();

    public void storeOtp(String email, String otp) {
        OtpDetails details = new OtpDetails(otp, System.currentTimeMillis());
        otpCache.put(email, details);
    }

    public OtpDetails getOtpDetails(String email) {
        return otpCache.get(email);
    }

    public void removeOtp(String email) {
        otpCache.remove(email);
    }

    public static class OtpDetails {

        private String otp;
        private long timestamp;

        public OtpDetails(String otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }

        public String getOtp() {
            return otp;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

}
