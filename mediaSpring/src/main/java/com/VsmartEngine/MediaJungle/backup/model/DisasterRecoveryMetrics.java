package com.VsmartEngine.MediaJungle.backup.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 2: Data Backup & Recovery | Task 5, 7, 8: DR Dashboard, RTO & RPO Monitoring
// Description: DisasterRecoveryMetrics entity tracking RTO target vs actual, RPO target vs actual data lag, readiness score, and node health.
@Entity
@Table(name = "dr_metrics")
public class DisasterRecoveryMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ISO 27001 | Task 7: RTO Monitoring
    private Long targetRtoMinutes = 240L; // 4 Hours default Target RTO

    private Long actualRtoMinutes = 45L; // Calculated actual restoration duration

    private String rtoStatus = "COMPLIANT"; // COMPLIANT, BREACHED

    // ISO 27001 | Task 8: RPO Monitoring
    private Long targetRpoMinutes = 60L; // 1 Hour default Target RPO

    private Long actualRpoLagMinutes = 15L; // Data lag since last backup

    private String rpoStatus = "COMPLIANT"; // COMPLIANT, BREACH_WARNING, BREACHED

    // ISO 27001 | Task 5: Disaster Recovery Dashboard
    private Integer readinessScorePercent = 98;

    private String drSiteStatus = "READY_FAILOVER"; // READY_FAILOVER, DEGRADED, DOWN

    private LocalDateTime updatedAt = LocalDateTime.now();

    public DisasterRecoveryMetrics() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTargetRtoMinutes() { return targetRtoMinutes; }
    public void setTargetRtoMinutes(Long targetRtoMinutes) { this.targetRtoMinutes = targetRtoMinutes; }

    public Long getActualRtoMinutes() { return actualRtoMinutes; }
    public void setActualRtoMinutes(Long actualRtoMinutes) { this.actualRtoMinutes = actualRtoMinutes; }

    public String getRtoStatus() { return rtoStatus; }
    public void setRtoStatus(String rtoStatus) { this.rtoStatus = rtoStatus; }

    public Long getTargetRpoMinutes() { return targetRpoMinutes; }
    public void setTargetRpoMinutes(Long targetRpoMinutes) { this.targetRpoMinutes = targetRpoMinutes; }

    public Long getActualRpoLagMinutes() { return actualRpoLagMinutes; }
    public void setActualRpoLagMinutes(Long actualRpoLagMinutes) { this.actualRpoLagMinutes = actualRpoLagMinutes; }

    public String getRpoStatus() { return rpoStatus; }
    public void setRpoStatus(String rpoStatus) { this.rpoStatus = rpoStatus; }

    public Integer getReadinessScorePercent() { return readinessScorePercent; }
    public void setReadinessScorePercent(Integer readinessScorePercent) { this.readinessScorePercent = readinessScorePercent; }

    public String getDrSiteStatus() { return drSiteStatus; }
    public void setDrSiteStatus(String drSiteStatus) { this.drSiteStatus = drSiteStatus; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
