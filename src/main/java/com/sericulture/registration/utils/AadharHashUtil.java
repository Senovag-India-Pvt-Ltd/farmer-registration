package com.sericulture.registration.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Produces the SHA-256 hash of an Aadhaar number in the exact format the FRUITS
 * GetFarmerByAadharHash API expects.
 *
 * Mirrors the C# reference in the Forest_API_Service_Document:
 *   SHA256 -> ComputeHash(UTF8 bytes) -> hex ("x2") per byte -> ToUpper()
 */
public final class AadharHashUtil {

    private AadharHashUtil() {
    }

    public static String hash(String aadharNo) {
        if (aadharNo == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] result = digest.digest(aadharNo.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(result.length * 2);
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed by the JVM spec; this should never happen.
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
