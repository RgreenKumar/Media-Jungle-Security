package com.VsmartEngine.MediaJungle.auditlogging.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 5: Audit Logging & Monitoring | Task 3: Audit Trail Repository
// Description: AuditTrailEntry entity storing central immutable system audit trails with detailed payload diffs for traceability and compliance.
@Entity
@Table(name = "audit_trail_entries")
public class AuditTrailEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventCategory; // AUTH, ACCESS_CONTROL, BACKUP, CONFIG, SECURITY_ALERT

    @Column(nullable = false)
    private String actor;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String targetResource;

    @Column(length = 2000)
    private String payloadDiff;

    private String ipAddress;

    @Column(nullable = false)
    private String status = "SUCCESS";

    private LocalDateTime timestamp = LocalDateTime.now();

    public AuditTrailEntry() {}

    public AuditTrailEntry(String eventCategory, String actor, String action, String targetResource, String payloadDiff, String ipAddress, String status) {
        this.eventCategory = eventCategory;
        this.actor = actor;
        this.action = action;
        this.targetResource = targetResource;
        this.payloadDiff = payloadDiff;
        this.ipAddress = ipAddress;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventCategory() { return eventCategory; }
    public void setEventCategory(String eventCategory) { this.eventCategory = eventCategory; }

    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getTargetResource() { return targetResource; }
    public void setTargetResource(String targetResource) { this.targetResource = targetResource; }

    public String getPayloadDiff() { return payloadDiff; }
    public void setPayloadDiff(String payloadDiff) { this.payloadDiff = payloadDiff; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
