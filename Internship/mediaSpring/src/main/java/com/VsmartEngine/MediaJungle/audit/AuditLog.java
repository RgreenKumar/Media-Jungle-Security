// ===========================================
// Internship Security Enhancement
// Feature : Audit Logging
// ISO27001 Control : Logging & Monitoring
// ===========================================
package com.VsmartEngine.MediaJungle.audit;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// ISO27001 Audit Trail: AuditLog JPA Entity mapping table audit_log
@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "role")
    private String role;

    @Column(name = "action")
    private String action;

    @Column(name = "module")
    private String module;

    @Column(name = "request_url")
    private String requestUrl;

    @Column(name = "http_method")
    private String httpMethod;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "status")
    private String status;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    public AuditLog() {
        this.timestamp = LocalDateTime.now();
    }

    public AuditLog(String username, String role, String action, String module, String requestUrl,
                    String httpMethod, String ipAddress, String status, String description) {
        this.username = username;
        this.role = role;
        this.action = action;
        this.module = module;
        this.requestUrl = requestUrl;
        this.httpMethod = httpMethod;
        this.ipAddress = ipAddress;
        this.status = status;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
