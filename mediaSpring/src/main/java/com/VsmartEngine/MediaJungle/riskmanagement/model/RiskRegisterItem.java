package com.VsmartEngine.MediaJungle.riskmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 4: Risk Management | Task 1, 2, 7: Risk Register & Assessment Engine
// Description: RiskRegisterItem entity tracking security risks, 5x5 Likelihood x Impact scoring, control effectiveness, and residual risk.
@Entity
@Table(name = "risk_register_items")
public class RiskRegisterItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String riskTitle;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String category; // INFRASTRUCTURE, APPLICATION_SECURITY, PRIVACY, AUTHENTICATION, COMPLIANCE

    // ISO 27001 Task 2: Risk Assessment Scoring Matrix (1 to 5 scale)
    @Column(nullable = false)
    private Integer likelihood = 3; // 1 (Rare) to 5 (Almost Certain)

    @Column(nullable = false)
    private Integer impact = 3; // 1 (Negligible) to 5 (Catastrophic)

    private Integer inherentRiskScore; // likelihood * impact (1 to 25)

    private String inherentRiskLevel; // LOW (1-5), MEDIUM (6-12), HIGH (13-19), CRITICAL (20-25)

    // ISO 27001 Task 7: Control Effectiveness & Residual Risk
    private Integer controlEffectivenessPercent = 70; // 0% to 100%

    private Integer residualRiskScore; // InherentScore * (1 - Effectiveness%)

    private String residualRiskStatus = "ACCEPTABLE"; // ACCEPTABLE, UNACCEPTABLE

    @Column(nullable = false)
    private String status = "IDENTIFIED"; // IDENTIFIED, MITIGATING, ACCEPTED, CLOSED

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime lastAssessedAt = LocalDateTime.now();

    public RiskRegisterItem() {}

    public RiskRegisterItem(String riskTitle, String description, String category, Integer likelihood, Integer impact, Integer controlEffectivenessPercent) {
        this.riskTitle = riskTitle;
        this.description = description;
        this.category = category;
        this.likelihood = likelihood != null ? likelihood : 3;
        this.impact = impact != null ? impact : 3;
        this.controlEffectivenessPercent = controlEffectivenessPercent != null ? controlEffectivenessPercent : 70;
        calculateRiskScores();
    }

    public void calculateRiskScores() {
        this.inherentRiskScore = this.likelihood * this.impact;
        if (this.inherentRiskScore <= 5) this.inherentRiskLevel = "LOW";
        else if (this.inherentRiskScore <= 12) this.inherentRiskLevel = "MEDIUM";
        else if (this.inherentRiskScore <= 19) this.inherentRiskLevel = "HIGH";
        else this.inherentRiskLevel = "CRITICAL";

        double factor = 1.0 - (this.controlEffectivenessPercent / 100.0);
        this.residualRiskScore = (int) Math.round(this.inherentRiskScore * factor);
        this.residualRiskStatus = this.residualRiskScore <= 8 ? "ACCEPTABLE" : "UNACCEPTABLE";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRiskTitle() { return riskTitle; }
    public void setRiskTitle(String riskTitle) { this.riskTitle = riskTitle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getLikelihood() { return likelihood; }
    public void setLikelihood(Integer likelihood) { this.likelihood = likelihood; calculateRiskScores(); }

    public Integer getImpact() { return impact; }
    public void setImpact(Integer impact) { this.impact = impact; calculateRiskScores(); }

    public Integer getInherentRiskScore() { return inherentRiskScore; }
    public String getInherentRiskLevel() { return inherentRiskLevel; }

    public Integer getControlEffectivenessPercent() { return controlEffectivenessPercent; }
    public void setControlEffectivenessPercent(Integer controlEffectivenessPercent) { this.controlEffectivenessPercent = controlEffectivenessPercent; calculateRiskScores(); }

    public Integer getResidualRiskScore() { return residualRiskScore; }
    public String getResidualRiskStatus() { return residualRiskStatus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastAssessedAt() { return lastAssessedAt; }
    public void setLastAssessedAt(LocalDateTime lastAssessedAt) { this.lastAssessedAt = lastAssessedAt; }
}
