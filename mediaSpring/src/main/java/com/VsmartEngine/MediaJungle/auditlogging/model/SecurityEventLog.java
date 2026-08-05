package com.VsmartEngine.MediaJungle.auditlogging.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 5: Audit Logging & Monitoring | Task 4: Security Event Logging
// Description: SecurityEventLog entity recording security incidents, suspicious activity detections, authentication failures, and attack attempts.
@Entity
@Table(name = "security_event_logs")
public class SecurityEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventType; // AUTH_FAILURE, RATE_LIMIT_EXCEEDED, SQLI_DETECTED, XSS_DETECTED, SESSION_FORCE_LOGOUT

    @Column(nullable = false)
    private String severity = "WARNING"; // INFO, WARNING, CRITICAL, ALERT

    @Column(length = 1000)
    private String eventDetails;

    private String sourceIp;

    private String userEmail;

    private LocalDateTime timestamp = LocalDateTime.now();

    public SecurityEventLog() {}

    public SecurityEventLog(String eventType, String severity, String eventDetails, String sourceIp, String userEmail) {
        this.eventType = eventType;
        this.severity = severity;
        this.eventDetails = eventDetails;
        this.sourceIp = sourceIp;
        this.userEmail = userEmail;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getEventDetails() { return eventDetails; }
    public void setEventDetails(String eventDetails) { this.eventDetails = eventDetails; }

    public String getSourceIp() { return sourceIp; }
    public void setSourceIp(String sourceIp) { this.sourceIp = sourceIp; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
