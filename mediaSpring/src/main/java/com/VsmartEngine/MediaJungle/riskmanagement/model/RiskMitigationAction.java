package com.VsmartEngine.MediaJungle.riskmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

// ISO 27001 | Module 4: Risk Management | Task 4: Risk Mitigation Tracker
// Description: RiskMitigationAction entity tracking security treatment plans, assigned owners, and target resolution dates.
@Entity
@Table(name = "risk_mitigation_actions")
public class RiskMitigationAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long riskId;

    @Column(nullable = false)
    private String mitigationTitle;

    @Column(length = 1000)
    private String actionDetails;

    @Column(nullable = false)
    private String assignedOwner;

    private LocalDate targetCompletionDate;

    @Column(nullable = false)
    private String status = "PLANNED"; // PLANNED, IN_PROGRESS, COMPLETED

    private LocalDateTime createdAt = LocalDateTime.now();

    public RiskMitigationAction() {}

    public RiskMitigationAction(Long riskId, String mitigationTitle, String actionDetails, String assignedOwner, LocalDate targetCompletionDate) {
        this.riskId = riskId;
        this.mitigationTitle = mitigationTitle;
        this.actionDetails = actionDetails;
        this.assignedOwner = assignedOwner;
        this.targetCompletionDate = targetCompletionDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRiskId() { return riskId; }
    public void setRiskId(Long riskId) { this.riskId = riskId; }

    public String getMitigationTitle() { return mitigationTitle; }
    public void setMitigationTitle(String mitigationTitle) { this.mitigationTitle = mitigationTitle; }

    public String getActionDetails() { return actionDetails; }
    public void setActionDetails(String actionDetails) { this.actionDetails = actionDetails; }

    public String getAssignedOwner() { return assignedOwner; }
    public void setAssignedOwner(String assignedOwner) { this.assignedOwner = assignedOwner; }

    public LocalDate getTargetCompletionDate() { return targetCompletionDate; }
    public void setTargetCompletionDate(LocalDate targetCompletionDate) { this.targetCompletionDate = targetCompletionDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
