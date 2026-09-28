package com.VsmartEngine.MediaJungle.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Pattern;

/**
 * SOC 2 Control 1: Secure Password Storage
 * Utility for detecting BCrypt hashes, preventing double-hashing, and verifying passwords safely.
 */
public class PasswordSecurityUtil {

    private static final Pattern BCRYPT_PATTERN = Pattern.compile("^\\$2[aby]?\\$\\d{2}\\$[./A-Za-z0-9]{53}$");

    /**
     * Checks if a string is already a valid BCrypt hash.
     */
    public static boolean isBCryptHash(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return BCRYPT_PATTERN.matcher(password.trim()).matches();
    }

    /**
     * Encodes a password with BCrypt, unless it is already an encoded BCrypt hash.
     * Prevents double-hashing an existing hash.
     */
    public static String encodeIfRaw(PasswordEncoder encoder, String password) {
        if (password == null || password.trim().isEmpty()) {
            return null;
        }
        String clean = password.trim();
        if (isBCryptHash(clean)) {
            return clean;
        }
        return encoder.encode(clean);
    }

    /**
     * Verifies whether raw password matches stored hash safely.
     */
    public static boolean matches(PasswordEncoder encoder, String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) {
            return false;
        }
        if (!isBCryptHash(storedHash)) {
            return false;
        }
        return encoder.matches(rawPassword, storedHash);
    }
}
