package com.VsmartEngine.MediaJungle.security;

import com.VsmartEngine.MediaJungle.audit.AuditLogService;
import com.VsmartEngine.MediaJungle.model.AddUser;
import com.VsmartEngine.MediaJungle.repository.AddUserRepository;
import com.VsmartEngine.MediaJungle.userregister.JwtUtil;
import com.VsmartEngine.MediaJungle.userregister.UserRegister;
import com.VsmartEngine.MediaJungle.userregister.UserRegisterRepository;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * SOC 2 Control 4: Multi-Factor Authentication (MFA)
 * Endpoints for MFA setup, enrollment verification, login completion, and admin reset.
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v2/mfa")
public class MfaController {

    private static final Logger logger = LoggerFactory.getLogger(MfaController.class);

    @Autowired
    private TotpService totpService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRegisterRepository userRegisterRepository;

    @Autowired
    private AddUserRepository addUserRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 1. Initiate MFA Setup: Generates a secret and otpauth URI.
     */
    @PostMapping("/setup")
    public ResponseEntity<?> setupMfa(@RequestHeader("Authorization") String token) {
        try {
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired token"));
            }

            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            String secret = totpService.generateSecret();

            // Try UserRegister first
            Optional<UserRegister> userOpt = userRegisterRepository.findByEmail(username);
            if (userOpt.isEmpty()) {
                userOpt = userRegisterRepository.findByUsername(username);
            }

            if (userOpt.isPresent()) {
                UserRegister user = userOpt.get();
                user.setMfaSecret(secret);
                user.setMfaEnabled(false); // Pending verification
                userRegisterRepository.save(user);

                String uri = totpService.getOtpAuthUri(secret, user.getEmail(), "MediaJungle");
                return ResponseEntity.ok(Map.of(
                        "secret", secret,
                        "otpauthUri", uri,
                        "accountName", user.getEmail(),
                        "issuer", "MediaJungle"
                ));
            }

            // Try AddUser (admin/staff)
            Optional<AddUser> staffOpt = addUserRepository.findByUsername(username);
            if (staffOpt.isEmpty()) {
                staffOpt = addUserRepository.findByEmail(username);
            }

            if (staffOpt.isPresent()) {
                AddUser staff = staffOpt.get();
                staff.setMfaSecret(secret);
                staff.setMfaEnabled(false);
                addUserRepository.save(staff);

                String uri = totpService.getOtpAuthUri(secret, staff.getUsername(), "MediaJungle-Staff");
                return ResponseEntity.ok(Map.of(
                        "secret", secret,
                        "otpauthUri", uri,
                        "accountName", staff.getUsername(),
                        "issuer", "MediaJungle-Staff"
                ));
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        } catch (Exception e) {
            logger.error("Error during MFA setup", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error generating MFA setup"));
        }
    }

    /**
     * 2. Confirm & Enable MFA: Verifies first code and generates recovery backup codes.
     */
    @PostMapping("/enable")
    public ResponseEntity<?> enableMfa(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> request) {
        try {
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired token"));
            }

            String code = request.get("code");
            if (code == null || code.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "MFA verification code is required"));
            }

            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            // UserRegister check
            Optional<UserRegister> userOpt = userRegisterRepository.findByEmail(username);
            if (userOpt.isEmpty()) {
                userOpt = userRegisterRepository.findByUsername(username);
            }

            if (userOpt.isPresent()) {
                UserRegister user = userOpt.get();
                if (user.getMfaSecret() == null) {
                    return ResponseEntity.badRequest().body(Map.of("message", "MFA setup has not been initiated. Please call /setup first."));
                }

                if (!totpService.validateCode(user.getMfaSecret(), code)) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Invalid verification code"));
                }

                List<String> backupCodes = totpService.generateBackupCodes(8);
                user.setMfaBackupCodes(String.join(",", backupCodes));
                user.setMfaEnabled(true);
                userRegisterRepository.save(user);

                auditLogService.logAction(user.getUsername(), role, "MFA_ENABLED", "AUTH", "/api/v2/mfa/enable", "POST", null, "SUCCESS", "User enabled TOTP multi-factor authentication");

                return ResponseEntity.ok(Map.of(
                        "message", "Multi-factor authentication successfully enabled",
                        "backupCodes", backupCodes
                ));
            }

            // AddUser check
            Optional<AddUser> staffOpt = addUserRepository.findByUsername(username);
            if (staffOpt.isEmpty()) {
                staffOpt = addUserRepository.findByEmail(username);
            }

            if (staffOpt.isPresent()) {
                AddUser staff = staffOpt.get();
                if (staff.getMfaSecret() == null) {
                    return ResponseEntity.badRequest().body(Map.of("message", "MFA setup has not been initiated. Please call /setup first."));
                }

                if (!totpService.validateCode(staff.getMfaSecret(), code)) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Invalid verification code"));
                }

                List<String> backupCodes = totpService.generateBackupCodes(8);
                staff.setMfaBackupCodes(String.join(",", backupCodes));
                staff.setMfaEnabled(true);
                addUserRepository.save(staff);

                auditLogService.logAction(staff.getUsername(), role, "MFA_ENABLED", "AUTH", "/api/v2/mfa/enable", "POST", null, "SUCCESS", "Staff member enabled TOTP multi-factor authentication");

                return ResponseEntity.ok(Map.of(
                        "message", "Multi-factor authentication successfully enabled",
                        "backupCodes", backupCodes
                ));
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        } catch (Exception e) {
            logger.error("Error enabling MFA", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error enabling MFA"));
        }
    }

    /**
     * 3. Complete MFA Login: Verifies TOTP code or single-use backup code and issues final access JWT.
     */
    @PostMapping("/verify-login")
    public ResponseEntity<?> verifyLoginMfa(@RequestBody Map<String, String> request) {
        try {
            String tempToken = request.get("tempToken");
            String code = request.get("code");

            if (tempToken == null || code == null || code.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Temporary token and MFA code are required"));
            }

            Claims claims = jwtUtil.getMfaClaims(tempToken);
            if (claims == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired MFA session. Please log in again."));
            }

            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);
            String userType = claims.get("userType", String.class);
            Number userIdNum = claims.get("userId", Number.class);
            Long userId = userIdNum != null ? userIdNum.longValue() : null;

            String cleanCode = code.trim().toUpperCase();

            if ("USER".equalsIgnoreCase(userType)) {
                Optional<UserRegister> userOpt = userRegisterRepository.findById(userId);
                if (userOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
                }
                UserRegister user = userOpt.get();

                boolean validTotp = totpService.validateCode(user.getMfaSecret(), cleanCode);
                boolean validBackup = false;

                if (!validTotp && user.getMfaBackupCodes() != null) {
                    List<String> codes = new ArrayList<>(Arrays.asList(user.getMfaBackupCodes().split(",")));
                    if (codes.remove(cleanCode)) {
                        validBackup = true;
                        user.setMfaBackupCodes(String.join(",", codes));
                        userRegisterRepository.save(user);
                    }
                }

                if (!validTotp && !validBackup) {
                    auditLogService.logAction(user.getUsername(), role, "MFA_FAILURE", "AUTH", "/api/v2/mfa/verify-login", "POST", null, "FAILURE", "MFA verification failed for user: " + user.getEmail());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid authentication code"));
                }

                String finalJwt = jwtUtil.generateToken(user.getEmail(), role);
                auditLogService.logAction(user.getUsername(), role, "MFA_LOGIN_SUCCESS", "AUTH", "/api/v2/mfa/verify-login", "POST", null, "SUCCESS", "User completed MFA login successfully");

                Map<String, Object> resp = new HashMap<>();
                resp.put("token", finalJwt);
                resp.put("Token", finalJwt);
                resp.put("message", "Login successful");
                resp.put("name", user.getUsername());
                resp.put("email", user.getEmail());
                resp.put("userId", user.getId());
                resp.put("role", role);
                if (validBackup) {
                    resp.put("warning", "A single-use backup code was used to authenticate.");
                }
                return ResponseEntity.ok(resp);

            } else {
                // STAFF / ADMIN
                Optional<AddUser> staffOpt = addUserRepository.findById(userId);
                if (staffOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Admin user not found"));
                }
                AddUser staff = staffOpt.get();

                boolean validTotp = totpService.validateCode(staff.getMfaSecret(), cleanCode);
                boolean validBackup = false;

                if (!validTotp && staff.getMfaBackupCodes() != null) {
                    List<String> codes = new ArrayList<>(Arrays.asList(staff.getMfaBackupCodes().split(",")));
                    if (codes.remove(cleanCode)) {
                        validBackup = true;
                        staff.setMfaBackupCodes(String.join(",", codes));
                        addUserRepository.save(staff);
                    }
                }

                if (!validTotp && !validBackup) {
                    auditLogService.logAction(staff.getUsername(), role, "MFA_FAILURE", "AUTH", "/api/v2/mfa/verify-login", "POST", null, "FAILURE", "Admin MFA verification failed for user: " + staff.getUsername());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid authentication code"));
                }

                String finalJwt = jwtUtil.generateToken(staff.getUsername(), role);
                auditLogService.logAction(staff.getUsername(), role, "MFA_LOGIN_SUCCESS", "AUTH", "/api/v2/mfa/verify-login", "POST", null, "SUCCESS", "Admin completed MFA login successfully");

                Map<String, Object> resp = new HashMap<>();
                resp.put("token", finalJwt);
                resp.put("Token", finalJwt);
                resp.put("message", "Login successful");
                resp.put("UserName", staff.getUsername());
                resp.put("Email", staff.getEmail());
                resp.put("AdminId", staff.getId());
                resp.put("Role", role);
                if (validBackup) {
                    resp.put("warning", "A single-use backup code was used to authenticate.");
                }
                return ResponseEntity.ok(resp);
            }
        } catch (Exception e) {
            logger.error("Error during MFA verification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error verifying MFA"));
        }
    }

    /**
     * 4. Disable MFA: Allows user to disable their own MFA with password confirmation.
     */
    @PostMapping("/disable")
    public ResponseEntity<?> disableMfa(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> request) {
        try {
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired token"));
            }

            String password = request.get("password");
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Password is required to disable MFA"));
            }

            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            Optional<UserRegister> userOpt = userRegisterRepository.findByEmail(username);
            if (userOpt.isEmpty()) {
                userOpt = userRegisterRepository.findByUsername(username);
            }

            if (userOpt.isPresent()) {
                UserRegister user = userOpt.get();
                if (!PasswordSecurityUtil.matches(passwordEncoder, password, user.getPassword())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Incorrect password"));
                }

                user.setMfaEnabled(false);
                user.setMfaSecret(null);
                user.setMfaBackupCodes(null);
                userRegisterRepository.save(user);

                auditLogService.logAction(user.getUsername(), role, "MFA_DISABLED", "AUTH", "/api/v2/mfa/disable", "POST", null, "SUCCESS", "User disabled MFA");
                return ResponseEntity.ok(Map.of("message", "MFA successfully disabled"));
            }

            Optional<AddUser> staffOpt = addUserRepository.findByUsername(username);
            if (staffOpt.isEmpty()) {
                staffOpt = addUserRepository.findByEmail(username);
            }

            if (staffOpt.isPresent()) {
                AddUser staff = staffOpt.get();
                if (!PasswordSecurityUtil.matches(passwordEncoder, password, staff.getPassword())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Incorrect password"));
                }

                staff.setMfaEnabled(false);
                staff.setMfaSecret(null);
                staff.setMfaBackupCodes(null);
                addUserRepository.save(staff);

                auditLogService.logAction(staff.getUsername(), role, "MFA_DISABLED", "AUTH", "/api/v2/mfa/disable", "POST", null, "SUCCESS", "Staff disabled MFA");
                return ResponseEntity.ok(Map.of("message", "MFA successfully disabled"));
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        } catch (Exception e) {
            logger.error("Error disabling MFA", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error disabling MFA"));
        }
    }

    /**
     * 5. Admin MFA Reset: Privileged administrator can reset/disable MFA for locked-out accounts.
     */
    @PostMapping("/admin/reset")
    public ResponseEntity<?> adminResetMfa(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        try {
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired token"));
            }

            String adminRole = jwtUtil.getRoleFromToken(token);
            if (!"ADMIN".equalsIgnoreCase(adminRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Administrator privileges required to reset MFA"));
            }

            String targetType = (String) request.get("userType"); // USER or STAFF
            Number targetIdNum = (Number) request.get("userId");
            if (targetIdNum == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "userId is required"));
            }
            Long targetId = targetIdNum.longValue();
            String adminUsername = jwtUtil.getUsernameFromToken(token);

            if ("STAFF".equalsIgnoreCase(targetType)) {
                Optional<AddUser> staffOpt = addUserRepository.findById(targetId);
                if (staffOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Staff user not found"));
                }
                AddUser staff = staffOpt.get();
                staff.setMfaEnabled(false);
                staff.setMfaSecret(null);
                staff.setMfaBackupCodes(null);
                addUserRepository.save(staff);

                auditLogService.logAction(adminUsername, adminRole, "ADMIN_MFA_RESET", "ADMIN", "/api/v2/mfa/admin/reset", "POST", null, "SUCCESS", "Administrator reset MFA for staff user: " + staff.getUsername());
                return ResponseEntity.ok(Map.of("message", "MFA reset successfully for staff: " + staff.getUsername()));
            } else {
                Optional<UserRegister> userOpt = userRegisterRepository.findById(targetId);
                if (userOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Registered user not found"));
                }
                UserRegister user = userOpt.get();
                user.setMfaEnabled(false);
                user.setMfaSecret(null);
                user.setMfaBackupCodes(null);
                userRegisterRepository.save(user);

                auditLogService.logAction(adminUsername, adminRole, "ADMIN_MFA_RESET", "ADMIN", "/api/v2/mfa/admin/reset", "POST", null, "SUCCESS", "Administrator reset MFA for user: " + user.getEmail());
                return ResponseEntity.ok(Map.of("message", "MFA reset successfully for user: " + user.getEmail()));
            }
        } catch (Exception e) {
            logger.error("Error in adminResetMfa", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error resetting MFA: " + e.getMessage()));
        }
    }
}
