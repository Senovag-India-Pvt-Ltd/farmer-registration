package com.sericulture.registration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class OtpService {

    private final Cache otpCache;

    @Autowired
    public OtpService(CacheManager cacheManager) {
        this.otpCache = cacheManager.getCache("otpCache");
    }

    public void storeOtp(String key) {
        String otp = generateOTP();
        System.out.println(otp);
        otpCache.put(key, otp);
    }

    public String getOtp(String key) {
        return otpCache.get(key, String.class);
    }

    public void removeOtp(String key) {
        otpCache.evict(key);
    }

    public Boolean verifyOtp(String key, String otp) {
        String storedOtp = getOtp(key);
        if(storedOtp.equals(otp)){
            return true;
        }else{
            return false;
        }
    }

    private static final int OTP_LENGTH = 6;
    private static String generateOTP() {
        // Define the characters to use in the OTP
        String characters = "0123456789";

        // Use SecureRandom for better security
        SecureRandom random = new SecureRandom();

        StringBuilder otp = new StringBuilder(OTP_LENGTH);

        // Generate OTP of the specified length
        for (int i = 0; i < OTP_LENGTH; i++) {
            int index = random.nextInt(characters.length());
            otp.append(characters.charAt(index));
        }

        return otp.toString();
    }
}
