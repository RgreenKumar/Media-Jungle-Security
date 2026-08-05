package com.VsmartEngine.MediaJungle.auditlogging.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 5: Audit Logging & Monitoring | Task 6: Alert Management System
// Description: SecurityAlert entity tracking system security alerts, severity levels, and resolution lifecycle status.
@Entity
@Table(name = "security_alerts")
public class SecurityAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String alertTitle;

    @Column(nullable = false)
    private String severity = "HIGH"; // CRITICAL, HIGH, MEDIUM, LOW

    @Column(length = 1000)
    private String alertSummary;

    @Column(nullable = false)
    private String status = "NEW"; // NEW, ACKNOWLEDGED, RESOLVED

    private String assignedTo;

    private LocalDateTime triggeredAt = LocalDateTime.now();

    private LocalDateTime resolvedAt;

    public SecurityAlert() {}

    public SecurityAlert(String alertTitle, String severity, String alertSummary, String assignedTo) {
        this.alertTitle = alertTitle;
        this.severity = severity;
        this.alertSummary = alertSummary;
        this.assignedTo = assignedTo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAlertTitle() { return alertTitle; }
    public void setAlertTitle(String alertTitle) { this.alertTitle = alertTitle; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getAlertSummary() { return alertSummary; }
    public void setAlertSummary(String alertSummary) { this.alertSummary = alertSummary; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public LocalDateTime getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(LocalDateTime triggeredAt) { this.triggeredAt = triggeredAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}
