package com.VsmartEngine.MediaJungle.riskmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 4: Risk Management | Task 6: Control Effectiveness Review
// Description: ControlEffectivenessReview entity recording effectiveness ratings (0-100%) and safeguard audit notes.
@Entity
@Table(name = "control_effectiveness_reviews")
public class ControlEffectivenessReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String controlName; // e.g. AES-256 Backup Encryption, MFA OTP Verification, API Token Rate Limiting

    @Column(nullable = false)
    private Integer effectivenessPercent = 85; // 0% to 100%

    @Column(length = 1000)
    private String evaluationNotes;

    private String testedBy;

    private LocalDateTime testDate = LocalDateTime.now();

    public ControlEffectivenessReview() {}

    public ControlEffectivenessReview(String controlName, Integer effectivenessPercent, String evaluationNotes, String testedBy) {
        this.controlName = controlName;
        this.effectivenessPercent = effectivenessPercent;
        this.evaluationNotes = evaluationNotes;
        this.testedBy = testedBy;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getControlName() { return controlName; }
    public void setControlName(String controlName) { this.controlName = controlName; }

    public Integer getEffectivenessPercent() { return effectivenessPercent; }
    public void setEffectivenessPercent(Integer effectivenessPercent) { this.effectivenessPercent = effectivenessPercent; }

    public String getEvaluationNotes() { return evaluationNotes; }
    public void setEvaluationNotes(String evaluationNotes) { this.evaluationNotes = evaluationNotes; }

    public String getTestedBy() { return testedBy; }
    public void setTestedBy(String testedBy) { this.testedBy = testedBy; }

    public LocalDateTime getTestDate() { return testDate; }
    public void setTestDate(LocalDateTime testDate) { this.testDate = testDate; }
}
