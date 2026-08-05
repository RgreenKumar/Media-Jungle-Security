package com.VsmartEngine.MediaJungle.codesecurity.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// ISO 27001 | Module 3: Code Level Security | Task 5: CSRF Protection
// Description: Service generating, storing, and validating cryptographic CSRF tokens for state-changing HTTP requests.
@Service
public class CsrfTokenService {

    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateCsrfToken(String sessionId) {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        tokenStore.put(sessionId, token);
        return token;
    }

    public boolean validateCsrfToken(String sessionId, String clientToken) {
        if (sessionId == null || clientToken == null) return false;
        String storedToken = tokenStore.get(sessionId);
        return clientToken.equals(storedToken);
    }
}
