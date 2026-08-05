package com.VsmartEngine.MediaJungle.accessmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 1: Access Management | Task 5: Session Monitoring
// Description: UserSession entity tracking active user login sessions, client IP, user agent, and session state for monitoring and remote termination.
@Entity
@Table(name = "user_sessions")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userEmail;

    @Column(length = 500)
    private String tokenHash;

    private String ipAddress;

    private String userAgent;

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE, TERMINATED, EXPIRED

    private LocalDateTime loginTime = LocalDateTime.now();

    private LocalDateTime lastActivityTime = LocalDateTime.now();

    public UserSession() {}

    public UserSession(String userEmail, String tokenHash, String ipAddress, String userAgent) {
        this.userEmail = userEmail;
        this.tokenHash = tokenHash;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }

    public LocalDateTime getLastActivityTime() { return lastActivityTime; }
    public void setLastActivityTime(LocalDateTime lastActivityTime) { this.lastActivityTime = lastActivityTime; }
}
