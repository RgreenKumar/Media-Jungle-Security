package com.VsmartEngine.MediaJungle.accessmanagement.controller;

import com.VsmartEngine.MediaJungle.accessmanagement.model.*;
import com.VsmartEngine.MediaJungle.accessmanagement.service.AccessManagementService;
import com.VsmartEngine.MediaJungle.userregister.UserRegister;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v2/access-management")
public class AccessManagementController {

    @Autowired
    private AccessManagementService accessManagementService;

    // ISO 27001 | Module 1: Access Management | Task 1: User Role Management
    // Description: Updates user access control role and returns role details.
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long userId,
            @RequestBody Map<String, String> requestBody,
            HttpServletRequest request) {
        try {
            String newRole = requestBody.get("role");
            String adminEmail = requestBody.getOrDefault("adminEmail", "admin@mediajungle.com");
            UserRegister updated = accessManagementService.updateUserRole(userId, newRole, adminEmail, request.getRemoteAddr());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ISO 27001 | Module 1: Access Management | Task 2: Access Approval Workflow
    // Description: Endpoint for creating and managing access requests and approvals.
    @PostMapping("/approval-requests/submit")
    public ResponseEntity<?> submitAccessRequest(@RequestBody Map<String, String> body, HttpServletRequest request) {
        AccessRequest req = accessManagementService.createAccessRequest(
            body.get("requesterEmail"),
            body.get("requestedRole"),
            body.get("reason"),
            request.getRemoteAddr()
        );
        return ResponseEntity.ok(req);
    }

    @PutMapping("/approval-requests/{id}/review")
    public ResponseEntity<?> reviewAccessRequest(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        AccessRequest req = accessManagementService.reviewAccessRequest(
            id,
            body.get("status"),
            body.getOrDefault("reviewerEmail", "admin@mediajungle.com"),
            request.getRemoteAddr()
        );
        return ResponseEntity.ok(req);
    }

    @GetMapping("/approval-requests")
    public ResponseEntity<List<AccessRequest>> getAccessRequests() {
        return ResponseEntity.ok(accessManagementService.getAllAccessRequests());
    }

    // ISO 27001 | Module 1: Access Management | Task 3: Multi-Factor Authentication
    // Description: Endpoints for initializing TOTP MFA secret and verifying OTP codes.
    @PostMapping("/mfa/setup")
    public ResponseEntity<?> setupMfa(@RequestBody Map<String, String> body) {
        try {
            Map<String, String> res = accessManagementService.generateMfaSecret(body.get("email"));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/mfa/verify")
    public ResponseEntity<?> verifyMfa(@RequestBody Map<String, String> body, HttpServletRequest request) {
        boolean valid = accessManagementService.verifyAndEnableMfa(body.get("email"), body.get("code"), request.getRemoteAddr());
        if (valid) {
            return ResponseEntity.ok(Map.of("message", "MFA verified and enabled successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid MFA verification code"));
        }
    }

    // ISO 27001 | Module 1: Access Management | Task 4: Privileged Access Management
    // Description: Endpoints for initiating and monitoring PAM elevated sessions.
    @PostMapping("/pam/elevate")
    public ResponseEntity<?> startPamSession(@RequestBody Map<String, String> body, HttpServletRequest request) {
        PrivilegedSession session = accessManagementService.startPrivilegedSession(
            body.get("adminEmail"),
            request.getRemoteAddr(),
            body.getOrDefault("role", "SYSTEM_ADMIN"),
            body.get("justification")
        );
        return ResponseEntity.ok(session);
    }

    @GetMapping("/pam/active-sessions")
    public ResponseEntity<List<PrivilegedSession>> getActivePamSessions() {
        return ResponseEntity.ok(accessManagementService.getActivePrivilegedSessions());
    }

    // ISO 27001 | Module 1: Access Management | Task 5: Session Monitoring
    // Description: Endpoints for listing active user sessions and forcing remote session termination.
    @GetMapping("/sessions/active")
    public ResponseEntity<List<UserSession>> getActiveSessions() {
        return ResponseEntity.ok(accessManagementService.getActiveSessions());
    }

    @DeleteMapping("/sessions/terminate/{id}")
    public ResponseEntity<?> terminateSession(@PathVariable Long id, HttpServletRequest request) {
        boolean success = accessManagementService.terminateSession(id, "admin@mediajungle.com", request.getRemoteAddr());
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Session terminated successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ISO 27001 | Module 1: Access Management | Task 6: User Provisioning
    // Description: Endpoint for automated user account onboarding and role assignment.
    @PostMapping("/provision")
    public ResponseEntity<?> provisionUser(@RequestBody Map<String, String> body, HttpServletRequest request) {
        try {
            UserRegister user = accessManagementService.provisionUser(
                body.get("username"),
                body.get("email"),
                body.get("password"),
                body.get("mobnum"),
                body.get("role"),
                body.getOrDefault("adminEmail", "admin@mediajungle.com"),
                request.getRemoteAddr()
            );
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ISO 27001 | Module 1: Access Management | Task 7: User Deprovisioning
    // Description: Endpoint for instantly revoking user access and disabling account.
    @PostMapping("/deprovision/{userId}")
    public ResponseEntity<?> deprovisionUser(@PathVariable Long userId, HttpServletRequest request) {
        boolean result = accessManagementService.deprovisionUser(userId, "admin@mediajungle.com", request.getRemoteAddr());
        if (result) {
            return ResponseEntity.ok(Map.of("message", "User account deprovisioned and permissions revoked successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ISO 27001 | Module 1: Access Management | Task 8: Access Reviews
    // Description: Endpoints for conducting and listing periodic access reviews.
    @PostMapping("/access-reviews/submit")
    public ResponseEntity<?> submitAccessReview(@RequestBody Map<String, String> body, HttpServletRequest request) {
        AccessReview review = accessManagementService.submitAccessReview(
            body.get("targetEmail"),
            body.get("reviewedRole"),
            body.getOrDefault("reviewerEmail", "auditor@mediajungle.com"),
            body.get("decision"),
            body.get("comments"),
            request.getRemoteAddr()
        );
        return ResponseEntity.ok(review);
    }

    @GetMapping("/access-reviews")
    public ResponseEntity<List<AccessReview>> getAccessReviews() {
        return ResponseEntity.ok(accessManagementService.getAllAccessReviews());
    }

    // ISO 27001 | Module 1: Access Management | Task 9: Login Activity Dashboard
    // Description: Endpoint for fetching login activity statistics and authentication metrics.
    @GetMapping("/login-activity/dashboard")
    public ResponseEntity<?> getLoginActivityDashboard() {
        return ResponseEntity.ok(accessManagementService.getLoginActivityStats());
    }

    // ISO 27001 | Module 1: Access Management | Task 10: Access Audit Module
    // Description: Endpoint for querying system access audit logs.
    @GetMapping("/audit-logs")
    public ResponseEntity<List<AccessAuditLog>> getAuditLogs() {
        return ResponseEntity.ok(accessManagementService.getRecentAuditLogs());
    }
}
