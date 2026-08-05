package com.VsmartEngine.MediaJungle.accessmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 1: Access Management | Task 10: Access Audit Module
// Description: AccessAuditLog entity recording security access events, privilege changes, administrative actions, and authorization decisions.
@Entity
@Table(name = "access_audit_logs")
public class AccessAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    private String userRole;

    @Column(nullable = false)
    private String action; // ROLE_CHANGE, MFA_SETUP, ACCOUNT_DEPROVISION, SESSION_TERMINATE, ACCESS_REQUEST, ACCESS_REVIEW

    @Column(nullable = false)
    private String resource;

    @Column(length = 1000)
    private String details;

    private String ipAddress;

    private String status = "SUCCESS"; // SUCCESS, DENIED, FAILED

    private LocalDateTime timestamp = LocalDateTime.now();

    public AccessAuditLog() {}

    public AccessAuditLog(String username, String userRole, String action, String resource, String details, String ipAddress, String status) {
        this.username = username;
        this.userRole = userRole;
        this.action = action;
        this.resource = resource;
        this.details = details;
        this.ipAddress = ipAddress;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
