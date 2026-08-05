package com.VsmartEngine.MediaJungle.riskmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 4: Risk Management | Task 5: Threat Analysis Module
// Description: ThreatAnalysisEntry entity categorizing security threat vectors according to the STRIDE threat model.
@Entity
@Table(name = "threat_analysis_entries")
public class ThreatAnalysisEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String threatName;

    @Column(nullable = false)
    private String strideCategory; // SPOOFING, TAMPERING, REPUDIATION, INFORMATION_DISCLOSURE, DENIAL_OF_SERVICE, ELEVATION_OF_PRIVILEGE

    @Column(nullable = false)
    private String targetAsset;

    @Column(length = 1000)
    private String threatVectorDetails;

    private String likelihood = "MEDIUM"; // HIGH, MEDIUM, LOW

    private LocalDateTime identifiedAt = LocalDateTime.now();

    public ThreatAnalysisEntry() {}

    public ThreatAnalysisEntry(String threatName, String strideCategory, String targetAsset, String threatVectorDetails, String likelihood) {
        this.threatName = threatName;
        this.strideCategory = strideCategory;
        this.targetAsset = targetAsset;
        this.threatVectorDetails = threatVectorDetails;
        this.likelihood = likelihood;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getThreatName() { return threatName; }
    public void setThreatName(String threatName) { this.threatName = threatName; }

    public String getStrideCategory() { return strideCategory; }
    public void setStrideCategory(String strideCategory) { this.strideCategory = strideCategory; }

    public String getTargetAsset() { return targetAsset; }
    public void setTargetAsset(String targetAsset) { this.targetAsset = targetAsset; }

    public String getThreatVectorDetails() { return threatVectorDetails; }
    public void setThreatVectorDetails(String threatVectorDetails) { this.threatVectorDetails = threatVectorDetails; }

    public String getLikelihood() { return likelihood; }
    public void setLikelihood(String likelihood) { this.likelihood = likelihood; }

    public LocalDateTime getIdentifiedAt() { return identifiedAt; }
    public void setIdentifiedAt(LocalDateTime identifiedAt) { this.identifiedAt = identifiedAt; }
}
