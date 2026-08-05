package com.VsmartEngine.MediaJungle.backup.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 2: Data Backup & Recovery | Task 6: Recovery Testing
// Description: RecoveryTestLog entity capturing simulated backup restoration tests, test duration, audit notes, and recovery status.
@Entity
@Table(name = "recovery_test_logs")
public class RecoveryTestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long backupRecordId;

    private String backupFilename;

    private Long durationSeconds;

    @Column(nullable = false)
    private String status = "SUCCESS"; // SUCCESS, FAILED, PARTIAL

    @Column(length = 1000)
    private String testNotes;

    private String performedBy;

    private LocalDateTime testTime = LocalDateTime.now();

    public RecoveryTestLog() {}

    public RecoveryTestLog(Long backupRecordId, String backupFilename, Long durationSeconds, String status, String testNotes, String performedBy) {
        this.backupRecordId = backupRecordId;
        this.backupFilename = backupFilename;
        this.durationSeconds = durationSeconds;
        this.status = status;
        this.testNotes = testNotes;
        this.performedBy = performedBy;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBackupRecordId() { return backupRecordId; }
    public void setBackupRecordId(Long backupRecordId) { this.backupRecordId = backupRecordId; }

    public String getBackupFilename() { return backupFilename; }
    public void setBackupFilename(String backupFilename) { this.backupFilename = backupFilename; }

    public Long getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Long durationSeconds) { this.durationSeconds = durationSeconds; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTestNotes() { return testNotes; }
    public void setTestNotes(String testNotes) { this.testNotes = testNotes; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public LocalDateTime getTestTime() { return testTime; }
    public void setTestTime(LocalDateTime testTime) { this.testTime = testTime; }
}
