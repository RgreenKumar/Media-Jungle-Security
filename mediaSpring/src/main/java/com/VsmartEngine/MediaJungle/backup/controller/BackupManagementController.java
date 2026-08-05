package com.VsmartEngine.MediaJungle.backup.controller;

import com.VsmartEngine.MediaJungle.backup.model.*;
import com.VsmartEngine.MediaJungle.backup.scheduler.BackupScheduler;
import com.VsmartEngine.MediaJungle.backup.service.BackupManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v2/backup")
public class BackupManagementController {

    @Autowired
    private BackupManagementService backupManagementService;

    @Autowired
    private BackupScheduler backupScheduler;

    // ISO 27001 | Module 2: Data Backup & Recovery | Task 1: Backup Management Module
    // Description: Endpoints for triggering manual backups and fetching backup archive records.
    @PostMapping("/trigger")
    public ResponseEntity<?> triggerBackup(@RequestBody(required = false) Map<String, Object> body) {
        String triggeredBy = (body != null && body.containsKey("triggeredBy")) ? body.get("triggeredBy").toString() : "ADMIN_MANUAL";
        Integer retentionDays = (body != null && body.containsKey("retentionDays")) ? Integer.parseInt(body.get("retentionDays").toString()) : 30;
        BackupRecord record = backupManagementService.createBackup(triggeredBy, retentionDays);
        return ResponseEntity.ok(record);
    }

    @GetMapping("/records")
    public ResponseEntity<List<BackupRecord>> getBackupRecords() {
        return ResponseEntity.ok(backupManagementService.getAllBackupRecords());
    }

    // ISO 27001 | Module 2: Data Backup & Recovery | Task 2: Automated Backup Scheduler
    // Description: Endpoints for querying scheduler status and toggling automated execution.
    @GetMapping("/scheduler/status")
    public ResponseEntity<?> getSchedulerStatus() {
        return ResponseEntity.ok(Map.of(
            "enabled", backupScheduler.isSchedulerEnabled(),
            "cronSchedule", "0 0 2 * * ?",
            "nextRun", "Daily at 02:00 AM"
        ));
    }

    @PostMapping("/scheduler/toggle")
    public ResponseEntity<?> toggleScheduler(@RequestBody Map<String, Boolean> body) {
        Boolean enabled = body.getOrDefault("enabled", true);
        backupScheduler.setSchedulerEnabled(enabled);
        return ResponseEntity.ok(Map.of("enabled", backupScheduler.isSchedulerEnabled(), "message", "Scheduler status updated"));
    }

    // ISO 27001 | Module 2: Data Backup & Recovery | Task 3: Backup Encryption
    // Description: Endpoint returning backup AES-256 encryption metadata and status.
    @GetMapping("/encryption-info")
    public ResponseEntity<?> getEncryptionInfo() {
        return ResponseEntity.ok(Map.of(
            "algorithm", "AES-256-GCM",
            "tagLengthBits", 128,
            "ivLengthBytes", 12,
            "status", "ACTIVE_ENCRYPTION_ENFORCED"
        ));
    }

    // ISO 27001 | Module 2: Data Backup & Recovery | Task 4: Backup Verification
    // Description: Endpoint triggering SHA-256 checksum verification on stored backup file.
    @PostMapping("/verify/{id}")
    public ResponseEntity<?> verifyBackupIntegrity(@PathVariable Long id) {
        boolean valid = backupManagementService.verifyBackupIntegrity(id);
        if (valid) {
            return ResponseEntity.ok(Map.of("status", "VERIFIED", "message", "Backup SHA-256 checksum verified successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("status", "CORRUPTED", "message", "Backup file integrity verification failed"));
        }
    }

    // ISO 27001 | Module 2: Data Backup & Recovery | Task 5: Disaster Recovery Dashboard
    // Description: Endpoint fetching DR readiness score, DR site status, and recovery metrics.
    @GetMapping("/dr-dashboard")
    public ResponseEntity<DisasterRecoveryMetrics> getDrDashboard() {
        return ResponseEntity.ok(backupManagementService.getDrMetrics());
    }

    // ISO 27001 | Module 2: Data Backup & Recovery | Task 6: Recovery Testing
    // Description: Endpoints for running simulated restoration tests and retrieving test logs.
    @PostMapping("/recovery-test/run")
    public ResponseEntity<?> runRecoveryTest(@RequestBody Map<String, Object> body) {
        Long backupId = Long.parseLong(body.get("backupId").toString());
        String performedBy = body.getOrDefault("performedBy", "auditor@mediajungle.com").toString();
        String notes = body.getOrDefault("notes", "Simulated backup recovery test").toString();

        RecoveryTestLog testLog = backupManagementService.performRecoveryTest(backupId, performedBy, notes);
        return ResponseEntity.ok(testLog);
    }

    @GetMapping("/recovery-test/logs")
    public ResponseEntity<List<RecoveryTestLog>> getRecoveryTestLogs() {
        return ResponseEntity.ok(backupManagementService.getAllRecoveryTestLogs());
    }

    // ISO 27001 | Module 2: Data Backup & Recovery | Task 7: RTO Monitoring
    // Description: Endpoint returning Recovery Time Objective (RTO) metrics.
    @GetMapping("/rto-metrics")
    public ResponseEntity<?> getRtoMetrics() {
        DisasterRecoveryMetrics dr = backupManagementService.getDrMetrics();
        return ResponseEntity.ok(Map.of(
            "targetRtoMinutes", dr.getTargetRtoMinutes(),
            "actualRtoMinutes", dr.getActualRtoMinutes(),
            "rtoStatus", dr.getRtoStatus()
        ));
    }

    // ISO 27001 | Module 2: Data Backup & Recovery | Task 8: RPO Monitoring
    // Description: Endpoint returning Recovery Point Objective (RPO) data lag metrics.
    @GetMapping("/rpo-metrics")
    public ResponseEntity<?> getRpoMetrics() {
        DisasterRecoveryMetrics dr = backupManagementService.getDrMetrics();
        return ResponseEntity.ok(Map.of(
            "targetRpoMinutes", dr.getTargetRpoMinutes(),
            "actualRpoLagMinutes", dr.getActualRpoLagMinutes(),
            "rpoStatus", dr.getRpoStatus()
        ));
    }

    // ISO 27001 | Module 2: Data Backup & Recovery | Task 9: Business Continuity Tracker
    // Description: Endpoint listing OTT service component continuity items and failover plans.
    @GetMapping("/bcp/items")
    public ResponseEntity<List<BusinessContinuityItem>> getBcpItems() {
        return ResponseEntity.ok(backupManagementService.getBcpItems());
    }

    // ISO 27001 | Module 2: Data Backup & Recovery | Task 10: Backup Retention Management
    // Description: Endpoint triggering manual purge of expired backup archives past retention policy.
    @PostMapping("/retention/purge")
    public ResponseEntity<?> purgeExpiredBackups() {
        int purgedCount = backupManagementService.purgeExpiredBackups();
        return ResponseEntity.ok(Map.of("purgedCount", purgedCount, "message", "Expired backup archives purged according to retention policy"));
    }
}
