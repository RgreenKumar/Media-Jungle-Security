package com.VsmartEngine.MediaJungle.auditlogging.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 5: Audit Logging & Monitoring | Task 1: User Activity Logging
// Description: UserActivityLog entity recording user system access, media playback, profile updates, and navigation events.
@Entity
@Table(name = "user_activity_logs")
public class UserActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String action; // PLAY_VIDEO, LIKE_AUDIO, UPDATE_PROFILE, LOGIN, SEARCH

    private String targetResource;

    private String ipAddress;

    private String userAgent;

    private LocalDateTime timestamp = LocalDateTime.now();

    public UserActivityLog() {}

    public UserActivityLog(String userEmail, String action, String targetResource, String ipAddress, String userAgent) {
        this.userEmail = userEmail;
        this.action = action;
        this.targetResource = targetResource;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getTargetResource() { return targetResource; }
    public void setTargetResource(String targetResource) { this.targetResource = targetResource; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
