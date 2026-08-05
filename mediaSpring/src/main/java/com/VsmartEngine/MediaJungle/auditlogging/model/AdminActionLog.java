package com.VsmartEngine.MediaJungle.auditlogging.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 5: Audit Logging & Monitoring | Task 2: Administrative Action Logging
// Description: AdminActionLog entity recording changes made to system configurations, security settings, user roles, and system parameters.
@Entity
@Table(name = "admin_action_logs")
public class AdminActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String adminEmail;

    @Column(nullable = false)
    private String actionType; // CONFIG_CHANGE, ROLE_UPDATE, MANUAL_BACKUP, USER_DEPROVISION, SITE_SETTING_EDIT

    @Column(nullable = false)
    private String targetSetting;

    @Column(length = 1000)
    private String changeDetails;

    private String ipAddress;

    private LocalDateTime timestamp = LocalDateTime.now();

    public AdminActionLog() {}

    public AdminActionLog(String adminEmail, String actionType, String targetSetting, String changeDetails, String ipAddress) {
        this.adminEmail = adminEmail;
        this.actionType = actionType;
        this.targetSetting = targetSetting;
        this.changeDetails = changeDetails;
        this.ipAddress = ipAddress;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getTargetSetting() { return targetSetting; }
    public void setTargetSetting(String targetSetting) { this.targetSetting = targetSetting; }

    public String getChangeDetails() { return changeDetails; }
    public void setChangeDetails(String changeDetails) { this.changeDetails = changeDetails; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
