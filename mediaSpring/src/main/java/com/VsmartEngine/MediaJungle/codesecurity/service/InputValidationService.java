package com.VsmartEngine.MediaJungle.codesecurity.service;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

// ISO 27001 | Module 3: Code Level Security | Task 2: Input Validation Framework
// Description: Centralized input validation service enforcing regex bounds, character set restrictions, and format compliance.
@Service
public class InputValidationService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9_.-]+$");
    private static final Pattern SAFE_TEXT_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s,_.-]+$");

    public boolean isValidEmail(String email) {
        if (email == null || email.length() > 254) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isAlphanumeric(String text) {
        if (text == null || text.trim().isEmpty()) return false;
        return ALPHANUMERIC_PATTERN.matcher(text).matches();
    }

    public boolean isSafeText(String text, int maxLength) {
        if (text == null) return true;
        if (text.length() > maxLength) return false;
        return SAFE_TEXT_PATTERN.matcher(text).matches();
    }

    public String sanitizeInput(String rawInput) {
        if (rawInput == null) return null;
        // Strip null bytes and illegal control characters
        return rawInput.replaceAll("\u0000", "").trim();
    }
}
