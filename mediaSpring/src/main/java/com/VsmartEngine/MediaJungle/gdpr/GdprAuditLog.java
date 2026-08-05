package com.VsmartEngine.MediaJungle.gdpr;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// GDPR-TASK-06: Audit trail entity. Every exercise of a data-subject right (access, erasure,
// consent change) is recorded here so the controller can demonstrate accountability and
// respond to regulator/DPA enquiries (GDPR Art. 5(2) "accountability" + Art. 30 records of processing).
@Entity
@Table(name = "gdpr_audit_log")
public class GdprAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    // e.g. "DATA_EXPORT", "DATA_ERASURE", "CONSENT_GRANTED", "CONSENT_WITHDRAWN"
    private String action;

    private String performedBy;

    private LocalDateTime timestamp;

    private String details;

    public GdprAuditLog() {
    }

    public GdprAuditLog(Long userId, String action, String performedBy, String details) {
        this.userId = userId;
        this.action = action;
        this.performedBy = performedBy;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
