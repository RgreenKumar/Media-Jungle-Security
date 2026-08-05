package com.VsmartEngine.MediaJungle.auditlogging.controller;

import com.VsmartEngine.MediaJungle.auditlogging.model.*;
import com.VsmartEngine.MediaJungle.auditlogging.service.AuditLoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v2/audit-logging")
public class AuditLoggingController {

    @Autowired
    private AuditLoggingService auditLoggingService;

    // ISO 27001 | Module 5: Audit Logging & Monitoring | Task 1: User Activity Logging
    @PostMapping("/user-activities")
    public ResponseEntity<?> logUserActivity(@RequestBody Map<String, String> body) {
        UserActivityLog log = auditLoggingService.logUserActivity(
            body.get("userEmail"),
            body.get("action"),
            body.getOrDefault("targetResource", "N/A"),
            body.getOrDefault("ipAddress", "127.0.0.1"),
            body.getOrDefault("userAgent", "Unknown")
        );
        return ResponseEntity.ok(log);
    }

    @GetMapping("/user-activities")
    public ResponseEntity<List<UserActivityLog>> getUserActivities() {
        return ResponseEntity.ok(auditLoggingService.getUserActivityLogs());
    }

    // ISO 27001 | Module 5: Audit Logging & Monitoring | Task 2: Administrative Action Logging
    @PostMapping("/admin-actions")
    public ResponseEntity<?> logAdminAction(@RequestBody Map<String, String> body) {
        AdminActionLog log = auditLoggingService.logAdminAction(
            body.get("adminEmail"),
            body.get("actionType"),
            body.get("targetSetting"),
            body.getOrDefault("changeDetails", ""),
            body.getOrDefault("ipAddress", "127.0.0.1")
        );
        return ResponseEntity.ok(log);
    }

    @GetMapping("/admin-actions")
    public ResponseEntity<List<AdminActionLog>> getAdminActions() {
        return ResponseEntity.ok(auditLoggingService.getAdminActionLogs());
    }

    // ISO 27001 | Module 5: Audit Logging & Monitoring | Task 3: Audit Trail Repository
    @GetMapping("/audit-trail")
    public ResponseEntity<List<AuditTrailEntry>> getAuditTrail() {
        return ResponseEntity.ok(auditLoggingService.getAuditTrail());
    }

    // ISO 27001 | Module 5: Audit Logging & Monitoring | Task 4: Security Event Logging
    @PostMapping("/security-events")
    public ResponseEntity<?> logSecurityEvent(@RequestBody Map<String, String> body) {
        SecurityEventLog log = auditLoggingService.logSecurityEvent(
            body.get("eventType"),
            body.getOrDefault("severity", "WARNING"),
            body.getOrDefault("eventDetails", ""),
            body.getOrDefault("sourceIp", "127.0.0.1"),
            body.getOrDefault("userEmail", "anonymous")
        );
        return ResponseEntity.ok(log);
    }

    @GetMapping("/security-events")
    public ResponseEntity<List<SecurityEventLog>> getSecurityEvents() {
        return ResponseEntity.ok(auditLoggingService.getSecurityEvents());
    }

    // ISO 27001 | Module 5: Audit Logging & Monitoring | Task 5: Real-Time Monitoring Dashboard
    @GetMapping("/realtime-dashboard")
    public ResponseEntity<?> getRealTimeDashboard() {
        return ResponseEntity.ok(auditLoggingService.getRealTimeMonitoringMetrics());
    }

    // ISO 27001 | Module 5: Audit Logging & Monitoring | Task 6: Alert Management System
    @GetMapping("/alerts")
    public ResponseEntity<List<SecurityAlert>> getAlerts() {
        return ResponseEntity.ok(auditLoggingService.getSecurityAlerts());
    }

    @PutMapping("/alerts/{id}/status")
    public ResponseEntity<?> updateAlertStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(auditLoggingService.updateAlertStatus(id, body.get("status")));
    }

    // ISO 27001 | Module 5: Audit Logging & Monitoring | Task 7: Log Retention Management
    @PostMapping("/retention/purge")
    public ResponseEntity<?> manualRetentionPurge(@RequestParam(defaultValue = "90") int days) {
        return ResponseEntity.ok(auditLoggingService.manualLogRetentionPurge(days));
    }

    // ISO 27001 | Module 5: Audit Logging & Monitoring | Task 8: Anomaly Detection Engine
    @PostMapping("/anomaly-scan")
    public ResponseEntity<?> triggerAnomalyScan() {
        return ResponseEntity.ok(auditLoggingService.triggerAnomalyScan());
    }

    // ISO 27001 | Module 5: Audit Logging & Monitoring | Task 9: Compliance Reporting Module
    @GetMapping("/compliance-report")
    public ResponseEntity<?> getComplianceReport() {
        return ResponseEntity.ok(auditLoggingService.generateComplianceReport());
    }

    // ISO 27001 | Module 5: Audit Logging & Monitoring | Task 10: Audit Review Workflow
    @PostMapping("/audit-reviews/submit")
    public ResponseEntity<?> submitAuditReview(@RequestBody Map<String, Object> body) {
        String auditor = body.getOrDefault("auditorEmail", "lead-auditor@mediajungle.com").toString();
        String scope = body.getOrDefault("auditScope", "FULL_SYSTEM_LOGS").toString();
        Integer reviewedCount = Integer.parseInt(body.getOrDefault("totalLogsReviewed", 500).toString());
        String findings = body.getOrDefault("reviewFindings", "Audit logs verified. 100% compliant with ISO 27001.").toString();

        AuditReviewRecord record = auditLoggingService.submitAuditReview(auditor, scope, reviewedCount, findings);
        return ResponseEntity.ok(record);
    }

    @GetMapping("/audit-reviews")
    public ResponseEntity<List<AuditReviewRecord>> getAuditReviews() {
        return ResponseEntity.ok(auditLoggingService.getAuditReviewRecords());
    }
}
