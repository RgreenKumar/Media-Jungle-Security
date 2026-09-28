package com.VsmartEngine.MediaJungle.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * SOC 2 Control 2: Strong Password Policy
 * Enforces standard password complexity requirements across registration,
 * admin creation, password reset, and user updates.
 */
@Service
public class PasswordPolicyService {

    @Value("${security.password.min-length:8}")
    private int minLength = 8;

    @Value("${security.password.require-uppercase:true}")
    private boolean requireUppercase = true;

    @Value("${security.password.require-lowercase:true}")
    private boolean requireLowercase = true;

    @Value("${security.password.require-digit:true}")
    private boolean requireDigit = true;

    @Value("${security.password.require-special:true}")
    private boolean requireSpecial = true;

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^A-Za-z0-9]");

    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors != null ? Collections.unmodifiableList(errors) : Collections.emptyList();
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getErrorMessage() {
            if (valid || errors.isEmpty()) {
                return "";
            }
            return "Password does not meet security requirements: " + String.join(", ", errors);
        }
    }

    public ValidationResult validate(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.trim().isEmpty()) {
            errors.add("Password must not be empty");
            return new ValidationResult(false, errors);
        }

        // If string is already a BCrypt hash, it has already passed policy validation
        if (PasswordSecurityUtil.isBCryptHash(password)) {
            return new ValidationResult(true, Collections.emptyList());
        }

        if (password.length() < minLength) {
            errors.add("Must be at least " + minLength + " characters long");
        }
        if (requireUppercase && !UPPERCASE_PATTERN.matcher(password).find()) {
            errors.add("Must contain at least one uppercase letter (A-Z)");
        }
        if (requireLowercase && !LOWERCASE_PATTERN.matcher(password).find()) {
            errors.add("Must contain at least one lowercase letter (a-z)");
        }
        if (requireDigit && !DIGIT_PATTERN.matcher(password).find()) {
            errors.add("Must contain at least one digit (0-9)");
        }
        if (requireSpecial && !SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            errors.add("Must contain at least one special character (!@#$%^&*...)");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    public boolean isValid(String password) {
        return validate(password).isValid();
    }

    public void enforce(String password) {
        ValidationResult result = validate(password);
        if (!result.isValid()) {
            throw new IllegalArgumentException(result.getErrorMessage());
        }
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }
}
