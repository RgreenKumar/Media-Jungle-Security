package com.VsmartEngine.MediaJungle.backup.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 2: Data Backup & Recovery | Task 9: Business Continuity Tracker
// Description: BusinessContinuityItem entity tracking OTT critical infrastructure component dependencies, operational health status, and failover plans.
@Entity
@Table(name = "bcp_items")
public class BusinessContinuityItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String componentName; // e.g. Primary PostgreSQL DB, Media Storage, Auth Service, DASH Video Transcoder

    private String criticalityLevel = "HIGH"; // CRITICAL, HIGH, MEDIUM, LOW

    private String healthStatus = "OPERATIONAL"; // OPERATIONAL, DEGRADED, FAILOVER_ACTIVE, DOWN

    private String failoverPlan;

    private LocalDateTime lastHealthCheck = LocalDateTime.now();

    public BusinessContinuityItem() {}

    public BusinessContinuityItem(String componentName, String criticalityLevel, String healthStatus, String failoverPlan) {
        this.componentName = componentName;
        this.criticalityLevel = criticalityLevel;
        this.healthStatus = healthStatus;
        this.failoverPlan = failoverPlan;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getComponentName() { return componentName; }
    public void setComponentName(String componentName) { this.componentName = componentName; }

    public String getCriticalityLevel() { return criticalityLevel; }
    public void setCriticalityLevel(String criticalityLevel) { this.criticalityLevel = criticalityLevel; }

    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }

    public String getFailoverPlan() { return failoverPlan; }
    public void setFailoverPlan(String failoverPlan) { this.failoverPlan = failoverPlan; }

    public LocalDateTime getLastHealthCheck() { return lastHealthCheck; }
    public void setLastHealthCheck(LocalDateTime lastHealthCheck) { this.lastHealthCheck = lastHealthCheck; }
}
