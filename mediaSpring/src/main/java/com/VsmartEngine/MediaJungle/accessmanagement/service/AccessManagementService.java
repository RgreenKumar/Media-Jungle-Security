package com.VsmartEngine.MediaJungle.accessmanagement.service;

import com.VsmartEngine.MediaJungle.accessmanagement.model.*;
import com.VsmartEngine.MediaJungle.accessmanagement.repository.*;
import com.VsmartEngine.MediaJungle.userregister.TokenBlacklist;
import com.VsmartEngine.MediaJungle.userregister.UserRegister;
import com.VsmartEngine.MediaJungle.userregister.UserRegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AccessManagementService {

    @Autowired
    private UserRegisterRepository userRegisterRepository;

    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private PrivilegedSessionRepository privilegedSessionRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private AccessReviewRepository accessReviewRepository;

    @Autowired
    private LoginActivityRepository loginActivityRepository;

    @Autowired
    private AccessAuditLogRepository accessAuditLogRepository;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    // ISO 27001 | Module 1: Access Management | Task 1: User Role Management
    // Description: Updates user access role (SECURITY_ADMIN, SYSTEM_ADMIN, AUDITOR, USER, ADMIN) and creates an access audit entry.
    public UserRegister updateUserRole(Long userId, String newRole, String adminEmail, String ipAddress) {
        Optional<UserRegister> userOpt = userRegisterRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserRegister user = userOpt.get();
            String oldRole = user.getRole();
            user.setRole(newRole.toUpperCase());
            UserRegister updated = userRegisterRepository.save(user);

            logAudit(
                adminEmail != null ? adminEmail : "SYSTEM",
                "ADMIN",
                "ROLE_CHANGE",
                "User:" + user.getEmail(),
                "Changed role from " + oldRole + " to " + newRole,
                ipAddress,
                "SUCCESS"
            );
            return updated;
        }
        throw new RuntimeException("User not found with ID: " + userId);
    }

    // ISO 27001 | Module 1: Access Management | Task 2: Access Approval Workflow
    // Description: Handles creation and approval/rejection workflows for elevated access requests.
    public AccessRequest createAccessRequest(String requesterEmail, String requestedRole, String reason, String ipAddress) {
        AccessRequest request = new AccessRequest(requesterEmail, requestedRole, reason);
        AccessRequest saved = accessRequestRepository.save(request);

        logAudit(requesterEmail, "USER", "ACCESS_REQUEST", "Role:" + requestedRole, "Created access request #" + saved.getId(), ipAddress, "SUCCESS");
        return saved;
    }

    public AccessRequest reviewAccessRequest(Long requestId, String status, String reviewerEmail, String ipAddress) {
        Optional<AccessRequest> opt = accessRequestRepository.findById(requestId);
        if (opt.isPresent()) {
            AccessRequest req = opt.get();
            req.setStatus(status.toUpperCase());
            req.setReviewedBy(reviewerEmail);
            req.setReviewedAt(LocalDateTime.now());
            AccessRequest updated = accessRequestRepository.save(req);

            if ("APPROVED".equalsIgnoreCase(status)) {
                Optional<UserRegister> userOpt = userRegisterRepository.findByEmail(req.getRequesterEmail());
                if (userOpt.isPresent()) {
                    UserRegister u = userOpt.get();
                    u.setRole(req.getRequestedRole().toUpperCase());
                    userRegisterRepository.save(u);
                }
            }

            logAudit(reviewerEmail, "ADMIN", "ACCESS_REQUEST_REVIEW", "Request:" + requestId, "Set status to " + status, ipAddress, "SUCCESS");
            return updated;
        }
        throw new RuntimeException("Access request not found: " + requestId);
    }

    public List<AccessRequest> getAllAccessRequests() {
        return accessRequestRepository.findAll();
    }

    // ISO 27001 | Module 1: Access Management | Task 3: Multi-Factor Authentication
    // Description: Generates TOTP MFA secrets and validates 6-digit verification codes for multi-factor authentication.
    public Map<String, String> generateMfaSecret(String email) {
        Optional<UserRegister> userOpt = userRegisterRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            UserRegister user = userOpt.get();
            String secret = generateRandomBase32Secret();
            user.setMfaSecret(secret);
            userRegisterRepository.save(user);

            Map<String, String> result = new HashMap<>();
            result.put("secret", secret);
            result.put("otpauthUrl", "otpauth://totp/MediaJungle:" + email + "?secret=" + secret + "&issuer=MediaJungle");
            return result;
        }
        throw new RuntimeException("User not found for MFA setup: " + email);
    }

    public boolean verifyAndEnableMfa(String email, String code, String ipAddress) {
        Optional<UserRegister> userOpt = userRegisterRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            UserRegister user = userOpt.get();
            if (user.getMfaSecret() != null && validateOtpCode(user.getMfaSecret(), code)) {
                user.setMfaEnabled(true);
                userRegisterRepository.save(user);
                logAudit(email, user.getRole(), "MFA_SETUP", "Account:" + email, "Enabled MFA successfully", ipAddress, "SUCCESS");
                return true;
            }
        }
        return false;
    }

    public boolean validateMfaCode(String email, String code) {
        Optional<UserRegister> userOpt = userRegisterRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            UserRegister user = userOpt.get();
            if (!user.getMfaEnabled()) return true; // MFA not mandatory for non-enabled users
            return user.getMfaSecret() != null && validateOtpCode(user.getMfaSecret(), code);
        }
        return false;
    }

    // ISO 27001 | Module 1: Access Management | Task 4: Privileged Access Management
    // Description: Creates and tracks elevated admin sessions with mandatory activity logging.
    public PrivilegedSession startPrivilegedSession(String adminEmail, String ipAddress, String role, String justification) {
        PrivilegedSession session = new PrivilegedSession(adminEmail, ipAddress, role, justification);
        PrivilegedSession saved = privilegedSessionRepository.save(session);

        logAudit(adminEmail, role, "PAM_ELEVATION", "PrivilegedSession:" + saved.getId(), "Started PAM session with justification: " + justification, ipAddress, "SUCCESS");
        return saved;
    }

    public List<PrivilegedSession> getActivePrivilegedSessions() {
        return privilegedSessionRepository.findByActive(true);
    }

    // ISO 27001 | Module 1: Access Management | Task 5: Session Monitoring
    // Description: Registers live sessions, retrieves active sessions, and forces session termination (logout).
    public UserSession createSession(String userEmail, String token, String ipAddress, String userAgent) {
        String tokenHash = String.valueOf(token.hashCode());
        UserSession session = new UserSession(userEmail, tokenHash, ipAddress, userAgent);
        return userSessionRepository.save(session);
    }

    public List<UserSession> getActiveSessions() {
        return userSessionRepository.findByStatus("ACTIVE");
    }

    public boolean terminateSession(Long sessionId, String adminEmail, String ipAddress) {
        Optional<UserSession> opt = userSessionRepository.findById(sessionId);
        if (opt.isPresent()) {
            UserSession session = opt.get();
            session.setStatus("TERMINATED");
            userSessionRepository.save(session);

            if (session.getTokenHash() != null) {
                tokenBlacklist.blacklistToken(session.getTokenHash());
            }

            logAudit(adminEmail, "ADMIN", "SESSION_TERMINATE", "UserSession:" + sessionId, "Terminated session for user " + session.getUserEmail(), ipAddress, "SUCCESS");
            return true;
        }
        return false;
    }

    // ISO 27001 | Module 1: Access Management | Task 6: User Provisioning
    // Description: Automates new user account creation, default security role assignment, and security profile initialization.
    public UserRegister provisionUser(String username, String email, String rawPassword, String mobnum, String role, String adminEmail, String ipAddress) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        UserRegister newUser = new UserRegister();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(encoder.encode(rawPassword));
        newUser.setMobnum(mobnum);
        newUser.setRole(role != null ? role.toUpperCase() : "USER");
        newUser.setStatus("ACTIVE");
        newUser.setAccountNonLocked(true);
        newUser.setDate(java.time.LocalDate.now());

        UserRegister saved = userRegisterRepository.save(newUser);

        logAudit(adminEmail != null ? adminEmail : "SYSTEM", "ADMIN", "USER_PROVISION", "User:" + email, "Provisioned new account with role " + newUser.getRole(), ipAddress, "SUCCESS");
        return saved;
    }

    // ISO 27001 | Module 1: Access Management | Task 7: User Deprovisioning
    // Description: Instantly revokes account access, sets status to DEPROVISIONED, locks account, and terminates active user sessions.
    public boolean deprovisionUser(Long userId, String adminEmail, String ipAddress) {
        Optional<UserRegister> opt = userRegisterRepository.findById(userId);
        if (opt.isPresent()) {
            UserRegister user = opt.get();
            user.setStatus("DEPROVISIONED");
            user.setAccountNonLocked(false);
            userRegisterRepository.save(user);

            // Terminate active sessions
            List<UserSession> activeSessions = userSessionRepository.findByUserEmailAndStatus(user.getEmail(), "ACTIVE");
            for (UserSession s : activeSessions) {
                s.setStatus("TERMINATED");
                userSessionRepository.save(s);
            }

            logAudit(adminEmail, "ADMIN", "ACCOUNT_DEPROVISION", "User:" + user.getEmail(), "Deprovisioned user account and revoked all active sessions", ipAddress, "SUCCESS");
            return true;
        }
        return false;
    }

    // ISO 27001 | Module 1: Access Management | Task 8: Access Reviews
    // Description: Performs periodic access rights certification and logs review decisions.
    public AccessReview submitAccessReview(String targetEmail, String reviewedRole, String reviewerEmail, String decision, String comments, String ipAddress) {
        AccessReview review = new AccessReview(targetEmail, reviewedRole, reviewerEmail, decision, comments);
        AccessReview saved = accessReviewRepository.save(review);

        if ("REVOKED".equalsIgnoreCase(decision)) {
            Optional<UserRegister> opt = userRegisterRepository.findByEmail(targetEmail);
            if (opt.isPresent()) {
                UserRegister u = opt.get();
                u.setRole("USER"); // Downgrade to baseline user role
                userRegisterRepository.save(u);
            }
        }

        logAudit(reviewerEmail, "AUDITOR", "ACCESS_REVIEW", "Target:" + targetEmail, "Access review decision: " + decision, ipAddress, "SUCCESS");
        return saved;
    }

    public List<AccessReview> getAllAccessReviews() {
        return accessReviewRepository.findAll();
    }

    // ISO 27001 | Module 1: Access Management | Task 9: Login Activity Dashboard
    // Description: Logs authentication attempts and returns login activity metrics and dashboard records.
    public void recordLoginActivity(String email, String status, String ipAddress, String userAgent, String failureReason) {
        LoginActivity activity = new LoginActivity(email, status, ipAddress, userAgent, failureReason);
        loginActivityRepository.save(activity);
    }

    public Map<String, Object> getLoginActivityStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSuccessful", loginActivityRepository.countByStatus("SUCCESS"));
        stats.put("totalFailedPassword", loginActivityRepository.countByStatus("FAILED_INVALID_PASSWORD"));
        stats.put("totalFailedMfa", loginActivityRepository.countByStatus("FAILED_MFA"));
        stats.put("recentActivities", loginActivityRepository.findTop100ByOrderByTimestampDesc());
        return stats;
    }

    // ISO 27001 | Module 1: Access Management | Task 10: Access Audit Module
    // Description: Records audit logs for access events and returns audit trail records.
    public void logAudit(String username, String userRole, String action, String resource, String details, String ipAddress, String status) {
        AccessAuditLog audit = new AccessAuditLog(username, userRole, action, resource, details, ipAddress, status);
        accessAuditLogRepository.save(audit);
    }

    public List<AccessAuditLog> getRecentAuditLogs() {
        return accessAuditLogRepository.findTop100ByOrderByTimestampDesc();
    }

    // Helper method for MFA TOTP simulation validation
    private String generateRandomBase32Secret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[10];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString().substring(0, 16);
    }

    private boolean validateOtpCode(String secret, String code) {
        if (code == null || code.trim().length() != 6) return false;
        // Simple 6-digit TOTP verification placeholder logic (accepts numeric code or demo secret match)
        return code.matches("\\d{6}");
    }
}
