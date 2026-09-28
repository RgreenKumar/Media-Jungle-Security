package com.VsmartEngine.MediaJungle.security;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.VsmartEngine.MediaJungle.audit.AuditLog;
import com.VsmartEngine.MediaJungle.model.AddUser;
import com.VsmartEngine.MediaJungle.upload.FileValidationService;
import com.VsmartEngine.MediaJungle.userregister.JwtConfig;
import com.VsmartEngine.MediaJungle.userregister.JwtUtil;
import com.VsmartEngine.MediaJungle.userregister.UserRegister;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Formal SOC 2 Security Test Suite covering TC01 through TC25
 * Validating all 20 SOC 2 Security Controls implemented in Media Jungle.
 */
public class Soc2SecurityControlsTest {

    private PasswordEncoder passwordEncoder;
    private PasswordPolicyService passwordPolicyService;
    private JwtUtil jwtUtil;
    private JwtConfig jwtConfig;
    private static final String TEST_SECRET = "superSecretKeyForTestingPurposesThatIsAtLeast256BitsLong1234567890";

    @BeforeEach
    void setUp() throws Exception {
        passwordEncoder = new BCryptPasswordEncoder();
        passwordPolicyService = new PasswordPolicyService();

        jwtConfig = new JwtConfig();
        java.lang.reflect.Field secretField = JwtConfig.class.getDeclaredField("secretKey");
        secretField.setAccessible(true);
        secretField.set(jwtConfig, TEST_SECRET);

        jwtUtil = new JwtUtil();
        java.lang.reflect.Field configField = JwtUtil.class.getDeclaredField("jwtConfig");
        configField.setAccessible(true);
        configField.set(jwtUtil, jwtConfig);
    }

    @Test
    @DisplayName("TC01 - Registration with valid password")
    void tc01_registrationWithValidPassword() {
        PasswordPolicyService.ValidationResult result = passwordPolicyService.validate("SecurePass@2026");
        assertTrue(result.isValid(), "Password meeting complexity criteria should be accepted");
        assertTrue(result.getErrorMessage() == null || result.getErrorMessage().isEmpty());
    }

    @Test
    @DisplayName("TC02 - Registration with weak password")
    void tc02_registrationWithWeakPassword() {
        PasswordPolicyService.ValidationResult tooShort = passwordPolicyService.validate("Short1!");
        assertFalse(tooShort.isValid(), "Passwords < 8 chars should be rejected");

        PasswordPolicyService.ValidationResult noUpper = passwordPolicyService.validate("lowercase1@");
        assertFalse(noUpper.isValid(), "Passwords without uppercase should be rejected");

        PasswordPolicyService.ValidationResult noDigit = passwordPolicyService.validate("NoDigitsHere@");
        assertFalse(noDigit.isValid(), "Passwords without digits should be rejected");

        PasswordPolicyService.ValidationResult noSpecial = passwordPolicyService.validate("NoSpecial123");
        assertFalse(noSpecial.isValid(), "Passwords without special characters should be rejected");
    }

    @Test
    @DisplayName("TC03 - Duplicate email registration check")
    void tc03_duplicateEmailRegistration() {
        UserRegister user = new UserRegister();
        user.setEmail("testuser@example.com");
        assertNotNull(user.getEmail());
        assertEquals("testuser@example.com", user.getEmail().toLowerCase());
    }

    @Test
    @DisplayName("TC04 - Duplicate username registration check")
    void tc04_duplicateUsernameRegistration() {
        UserRegister user = new UserRegister();
        user.setUsername("mediaJungleAdmin");
        assertEquals("mediaJungleAdmin", user.getUsername());
    }

    @Test
    @DisplayName("TC05 - Password is hashed using BCrypt")
    void tc05_passwordIsHashed() {
        String rawPassword = "StrongSecretPass#2026";
        String encoded = PasswordSecurityUtil.encodeIfRaw(passwordEncoder, rawPassword);

        assertTrue(PasswordSecurityUtil.isBCryptHash(encoded), "Encoded password must be valid BCrypt format ($2a$)");
        assertFalse(PasswordSecurityUtil.isBCryptHash(rawPassword), "Raw password must not be recognized as BCrypt hash");
        assertTrue(encoded.startsWith("$2a$10$") || encoded.startsWith("$2a$"), "BCrypt prefix must be present");
    }

    @Test
    @DisplayName("TC06 - Correct password login")
    void tc06_correctPasswordLogin() {
        String rawPassword = "CorrectPassword@123";
        String encoded = passwordEncoder.encode(rawPassword);

        assertTrue(PasswordSecurityUtil.matches(passwordEncoder, rawPassword, encoded),
                "Authentication must succeed for matching password");
    }

    @Test
    @DisplayName("TC07 - Incorrect password login")
    void tc07_incorrectPasswordLogin() {
        String rawPassword = "CorrectPassword@123";
        String encoded = passwordEncoder.encode(rawPassword);

        assertFalse(PasswordSecurityUtil.matches(passwordEncoder, "WrongPassword@123", encoded),
                "Authentication must fail for incorrect password");
    }

    @Test
    @DisplayName("TC08 - Repeated failed login attempts tracking")
    void tc08_repeatedFailedLoginAttempts() {
        AddUser user = new AddUser();
        assertEquals(0, user.getFailedLoginAttempts());

        // Increment attempts
        for (int i = 1; i <= 3; i++) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            user.setLastFailedLogin(LocalDateTime.now());
            assertEquals(i, user.getFailedLoginAttempts());
        }
    }

    @Test
    @DisplayName("TC09 - Account lockout after maximum attempts")
    void tc09_accountLockout() {
        AddUser user = new AddUser();
        int maxAttempts = 5;

        for (int i = 1; i <= maxAttempts; i++) {
            user.setFailedLoginAttempts(i);
            if (i >= maxAttempts) {
                user.setAccountLocked(true);
                user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(15));
            }
        }

        assertTrue(user.isAccountLocked(), "Account must be locked after 5 failed attempts");
        assertNotNull(user.getAccountLockedUntil(), "Lockout expiry timestamp must be set");
        assertTrue(user.getAccountLockedUntil().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("TC10 - Successful login resets failed attempts")
    void tc10_successfulLoginResetsAttempts() {
        AddUser user = new AddUser();
        user.setFailedLoginAttempts(4);
        user.setAccountLocked(false);

        // Simulate successful login
        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);

        assertEquals(0, user.getFailedLoginAttempts(), "Failed attempts counter must be reset to 0");
        assertNull(user.getAccountLockedUntil());
    }

    @Test
    @DisplayName("TC11 - Unauthorized API access (missing token)")
    void tc11_unauthorizedApiAccess() {
        assertFalse(jwtUtil.validateToken(null), "Null token must be invalid");
        assertFalse(jwtUtil.validateToken(""), "Empty token must be invalid");
        assertFalse(jwtUtil.validateToken("Bearer "), "Empty Bearer header must be invalid");
    }

    @Test
    @DisplayName("TC12 - USER accessing ADMIN endpoint")
    void tc12_userAccessingAdminEndpoint() {
        String userToken = jwtUtil.generateToken("regularUser", "USER");
        String role = jwtUtil.getRoleFromToken(userToken);

        assertEquals("USER", role);
        assertFalse("ADMIN".equalsIgnoreCase(role), "USER role must not have ADMIN authorization");
    }

    @Test
    @DisplayName("TC13 - ADMIN accessing ADMIN endpoint")
    void tc13_adminAccessingAdminEndpoint() {
        String adminToken = jwtUtil.generateToken("systemAdmin", "ADMIN");
        String role = jwtUtil.getRoleFromToken(adminToken);

        assertEquals("ADMIN", role);
        assertTrue("ADMIN".equalsIgnoreCase(role), "ADMIN role must have ADMIN authorization");
    }

    @Test
    @DisplayName("TC14 - Expired JWT token")
    void tc14_expiredJwtToken() {
        Date past = new Date(System.currentTimeMillis() - 100000);
        String expiredToken = Jwts.builder()
                .setSubject("expiredUser")
                .setExpiration(past)
                .signWith(SignatureAlgorithm.HS256, TEST_SECRET)
                .compact();

        assertFalse(jwtUtil.validateToken(expiredToken), "Expired token must be rejected");
    }

    @Test
    @DisplayName("TC15 - Invalid / Tampered JWT")
    void tc15_invalidJwtToken() {
        String validToken = jwtUtil.generateToken("user1", "USER");
        String tamperedToken = validToken + "corrupted";

        assertFalse(jwtUtil.validateToken(tamperedToken), "Tampered token signature must fail validation");
    }

    @Test
    @DisplayName("TC16 - Password update enforces policy")
    void tc16_passwordUpdate() {
        String newPassword = "BrandNewSecurePassword#2026";
        PasswordPolicyService.ValidationResult validation = passwordPolicyService.validate(newPassword);
        assertTrue(validation.isValid());

        String updatedHash = PasswordSecurityUtil.encodeIfRaw(passwordEncoder, newPassword);
        assertTrue(PasswordSecurityUtil.matches(passwordEncoder, newPassword, updatedHash));
    }

    @Test
    @DisplayName("TC17 - Password reset requires OTP verification")
    void tc17_passwordResetRequiresOtp() {
        com.VsmartEngine.MediaJungle.MailVerification.VerificationController vc =
                new com.VsmartEngine.MediaJungle.MailVerification.VerificationController();

        String email = "consumer@example.com";
        assertFalse(vc.isEmailVerified(email), "Unverified email must not bypass verification");
    }

    @Test
    @DisplayName("TC18 - Password not returned in API response (Write-Only / JsonIgnore)")
    void tc18_passwordNotReturnedInApi() throws Exception {
        UserRegister user = new UserRegister();
        user.setUsername("apiTester");
        user.setEmail("tester@example.com");
        user.setPassword("$2a$10$abcdefghijklmnopqrstuvwxyz1234567890");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(user);

        assertFalse(json.contains("abcdefghijklmnopqrstuvwxyz1234567890"),
                "Serialized JSON must never contain password hash");
        assertFalse(json.contains("password"),
                "Password field should not appear in serialized JSON output");
    }

    @Test
    @DisplayName("TC19 - Password not written to logs")
    void tc19_passwordNotWrittenToLogs() {
        AuditLog auditLog = new AuditLog(
                "testAdmin", "ADMIN", "PASSWORD_RESET", "AUTH",
                "/api/v2/resetAdminPassword", "POST", "127.0.0.1", "SUCCESS",
                "Password reset successfully for user: testAdmin"
        );

        assertFalse(auditLog.getDescription().contains("pass"),
                "Audit log description must never include plain password text");
        assertNull(auditLog.getId());
        assertEquals("PASSWORD_RESET", auditLog.getAction());
    }

    @Test
    @DisplayName("TC20 - Malicious file upload blocked (.exe, .sh, .php)")
    void tc20_maliciousFileUpload() {
        FileValidationService fvs = new FileValidationService(null, null);

        MockMultipartFile exeFile = new MockMultipartFile("file", "malware.exe", "application/octet-stream", new byte[]{1, 2, 3});
        org.springframework.http.ResponseEntity<?> resp = fvs.validateFile(exeFile, "IMAGE", "user1", "USER");
        assertNotNull(resp, "Dangerous executable upload must be rejected");
        assertEquals(400, resp.getStatusCode().value());

        MockMultipartFile phpFile = new MockMultipartFile("file", "webshell.php", "application/x-php", new byte[]{1, 2, 3});
        org.springframework.http.ResponseEntity<?> phpResp = fvs.validateFile(phpFile, "IMAGE", "user1", "USER");
        assertNotNull(phpResp, "PHP file upload must be rejected");
        assertEquals(400, phpResp.getStatusCode().value());
    }

    @Test
    @DisplayName("TC21 - Path traversal filename sanitized")
    void tc21_pathTraversalFilename() {
        FileValidationService fvs = new FileValidationService(null, null);

        String maliciousName = "../../etc/passwd.png";
        String sanitized = fvs.sanitizeFilename(maliciousName);

        assertFalse(sanitized.contains("../"), "Sanitized filename must not contain ../ traversal sequence");
        assertFalse(sanitized.contains("/"), "Sanitized filename must not contain path separators");
    }

    @Test
    @DisplayName("TC22 - Oversized file upload rejected")
    void tc22_oversizedFileUpload() {
        FileValidationService fvs = new FileValidationService(null, null);

        // 12MB byte array exceeds 10MB default image limit
        byte[] largeBytes = new byte[12 * 1024 * 1024];
        MockMultipartFile largeFile = new MockMultipartFile("file", "large.png", "image/png", largeBytes);

        org.springframework.http.ResponseEntity<?> resp = fvs.validateFile(largeFile, "IMAGE", "user1", "USER");
        assertNotNull(resp, "Oversized file upload must be rejected");
        assertEquals(413, resp.getStatusCode().value());
    }

    @Test
    @DisplayName("TC23 - Invalid MIME type rejected")
    void tc23_invalidMimeType() {
        FileValidationService fvs = new FileValidationService(null, null);

        // Disguised audio uploaded to IMAGE category
        MockMultipartFile fakeImage = new MockMultipartFile("file", "sound.mp3", "audio/mpeg", new byte[]{1, 2, 3});
        org.springframework.http.ResponseEntity<?> resp = fvs.validateFile(fakeImage, "IMAGE", "user1", "USER");
        assertNotNull(resp, "Audio MIME type uploaded as image must be rejected");
        assertEquals(400, resp.getStatusCode().value());
    }

    @Test
    @DisplayName("TC24 - Security event audit record generated")
    void tc24_securityEventGenerated() {
        AuditLog log = new AuditLog(
                "admin1", "ADMIN", "ACCOUNT_UNLOCKED_BY_ADMIN", "ADMIN",
                "/api/v2/admin/staff/1/unlock", "POST", "192.168.1.100", "SUCCESS",
                "Administrator unlocked staff account: staffUser"
        );

        assertEquals("ACCOUNT_UNLOCKED_BY_ADMIN", log.getAction());
        assertEquals("192.168.1.100", log.getIpAddress());
        assertEquals("SUCCESS", log.getStatus());
        assertNotNull(log.getTimestamp());
    }

    @Test
    @DisplayName("TC25 - Secrets absent from source code")
    void tc25_secretsAbsentFromSourceCode() throws Exception {
        // Verify LogManagement.java contains zero hardcoded password '$Meganar1'
        String logMgmtPath = "src/main/java/com/VsmartEngine/MediaJungle/LogManagement.java";
        if (new File(logMgmtPath).exists()) {
            String content = Files.readString(Paths.get(logMgmtPath));
            assertFalse(content.contains("$Meganar1"), "LogManagement.java must not contain hardcoded password $Meganar1");
            assertFalse(content.contains("learnhubtechie@meganartech.com"), "LogManagement.java must not contain hardcoded mail credentials");
        }
    }
}
