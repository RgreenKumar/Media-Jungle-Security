package com.VsmartEngine.MediaJungle.security;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * SOC 2 Control 4: Multi-Factor Authentication (MFA)
 * Pure Java implementation of RFC 6238 (TOTP) and RFC 4226 (HOTP).
 * Compatible with Google Authenticator, Microsoft Authenticator, Authy, etc.
 */
@Service
public class TotpService {

    private static final String BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final int TIME_STEP_SECONDS = 30;
    private static final int TOTP_DIGITS = 6;
    private static final int SECRET_BYTES = 20; // 160-bit secret for SHA-1
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generates a new random Base32-encoded 160-bit TOTP secret.
     */
    public String generateSecret() {
        byte[] buffer = new byte[SECRET_BYTES];
        SECURE_RANDOM.nextBytes(buffer);
        return encodeBase32(buffer);
    }

    /**
     * Builds standard otpauth URI for QR codes.
     */
    public String getOtpAuthUri(String secret, String accountName, String issuer) {
        String cleanIssuer = issuer != null ? issuer : "MediaJungle";
        String cleanAccount = accountName != null ? accountName : "user";
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
                urlEncode(cleanIssuer),
                urlEncode(cleanAccount),
                secret,
                urlEncode(cleanIssuer)
        );
    }

    /**
     * Validates a 6-digit TOTP code against secret, checking current, previous, and next intervals (±30s drift).
     */
    public boolean validateCode(String secret, String code) {
        if (secret == null || code == null) {
            return false;
        }
        String cleanCode = code.trim();
        if (cleanCode.length() != TOTP_DIGITS) {
            return false;
        }

        byte[] keyBytes = decodeBase32(secret);
        if (keyBytes.length == 0) {
            return false;
        }

        long currentInterval = System.currentTimeMillis() / 1000L / TIME_STEP_SECONDS;

        // Check current interval, -1 interval, and +1 interval (clock drift tolerance)
        for (int i = -1; i <= 1; i++) {
            long interval = currentInterval + i;
            String expected = generateCodeForInterval(keyBytes, interval);
            if (expected.equals(cleanCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates 8 single-use alphanumeric backup codes (format: XXXX-XXXX).
     */
    public List<String> generateBackupCodes(int count) {
        List<String> codes = new ArrayList<>(count);
        String charset = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        for (int i = 0; i < count; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 8; j++) {
                if (j == 4) {
                    sb.append('-');
                }
                sb.append(charset.charAt(SECURE_RANDOM.nextInt(charset.length())));
            }
            codes.add(sb.toString());
        }
        return codes;
    }

    private String generateCodeForInterval(byte[] key, long interval) {
        try {
            byte[] data = ByteBuffer.allocate(8).putLong(interval).array();
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "RAW"));
            byte[] hash = mac.doFinal(data);

            // Dynamic truncation
            int offset = hash[hash.length - 1] & 0x0F;
            int binary = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);

            int otp = binary % 1000000;
            return String.format("%06d", otp);
        } catch (Exception e) {
            return "";
        }
    }

    private String encodeBase32(byte[] data) {
        StringBuilder result = new StringBuilder((data.length * 8 + 4) / 5);
        int buffer = 0;
        int next = 0;
        int bitsLeft = 0;

        while (next < data.length) {
            buffer <<= 8;
            buffer |= (data[next++] & 0xFF);
            bitsLeft += 8;
            while (bitsLeft >= 5) {
                int index = (buffer >> (bitsLeft - 5)) & 0x1F;
                bitsLeft -= 5;
                result.append(BASE32_CHARS.charAt(index));
            }
        }
        if (bitsLeft > 0) {
            int index = (buffer << (5 - bitsLeft)) & 0x1F;
            result.append(BASE32_CHARS.charAt(index));
        }
        return result.toString();
    }

    private byte[] decodeBase32(String base32) {
        String clean = base32.toUpperCase().replaceAll("[^A-Z2-7]", "");
        int outputLength = clean.length() * 5 / 8;
        byte[] result = new byte[outputLength];

        int buffer = 0;
        int bitsLeft = 0;
        int index = 0;

        for (char c : clean.toCharArray()) {
            int val = BASE32_CHARS.indexOf(c);
            if (val < 0) continue;
            buffer <<= 5;
            buffer |= val;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                result[index++] = (byte) ((buffer >> (bitsLeft - 8)) & 0xFF);
                bitsLeft -= 8;
            }
        }
        return result;
    }

    private String urlEncode(String s) {
        try {
            return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8.name())
                    .replace("+", "%20");
        } catch (Exception e) {
            return s;
        }
    }
}
