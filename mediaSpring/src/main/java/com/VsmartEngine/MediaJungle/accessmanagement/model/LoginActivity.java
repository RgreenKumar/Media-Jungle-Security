package com.VsmartEngine.MediaJungle.accessmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 1: Access Management | Task 9: Login Activity Dashboard
// Description: LoginActivity entity capturing user login attempts, failure reasons, client IP, and timestamps for monitoring and anomaly detection.
@Entity
@Table(name = "login_activities")
public class LoginActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String status; // SUCCESS, FAILED_INVALID_PASSWORD, FAILED_MFA, FAILED_DEPROVISIONED

    private String ipAddress;

    private String userAgent;

    private String failureReason;

    private LocalDateTime timestamp = LocalDateTime.now();

    public LoginActivity() {}

    public LoginActivity(String email, String status, String ipAddress, String userAgent, String failureReason) {
        this.email = email;
        this.status = status;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.failureReason = failureReason;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
