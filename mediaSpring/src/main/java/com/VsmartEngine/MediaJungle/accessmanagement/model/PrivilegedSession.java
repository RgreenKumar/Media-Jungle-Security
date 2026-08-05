package com.VsmartEngine.MediaJungle.accessmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 1: Access Management | Task 4: Privileged Access Management
// Description: Entity tracking administrator elevated sessions and session elevation logs.
@Entity
@Table(name = "privileged_sessions")
public class PrivilegedSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String adminEmail;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private String role;

    private String elevationJustification;

    private Boolean active = true;

    private LocalDateTime startTime = LocalDateTime.now();

    private LocalDateTime lastActivityTime = LocalDateTime.now();

    private LocalDateTime endTime;

    public PrivilegedSession() {}

    public PrivilegedSession(String adminEmail, String ipAddress, String role, String elevationJustification) {
        this.adminEmail = adminEmail;
        this.ipAddress = ipAddress;
        this.role = role;
        this.elevationJustification = elevationJustification;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getElevationJustification() { return elevationJustification; }
    public void setElevationJustification(String elevationJustification) { this.elevationJustification = elevationJustification; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getLastActivityTime() { return lastActivityTime; }
    public void setLastActivityTime(LocalDateTime lastActivityTime) { this.lastActivityTime = lastActivityTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
