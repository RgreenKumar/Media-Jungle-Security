package com.VsmartEngine.MediaJungle.codesecurity.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 3: Code Level Security | Task 8: Code Review Workflow
// Description: CodeReviewEntry entity tracking pre-deployment peer security code reviews, reviewer sign-offs, and compliance verification.
@Entity
@Table(name = "code_reviews")
public class CodeReviewEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String featureTitle;

    @Column(nullable = false)
    private String authorEmail;

    @Column(nullable = false)
    private String reviewerEmail;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, APPROVED, CHANGES_REQUESTED

    @Column(length = 1000)
    private String securityChecklistComments;

    private Boolean staticAnalysisPassed = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime reviewedAt;

    public CodeReviewEntry() {}

    public CodeReviewEntry(String featureTitle, String authorEmail, String reviewerEmail, String securityChecklistComments) {
        this.featureTitle = featureTitle;
        this.authorEmail = authorEmail;
        this.reviewerEmail = reviewerEmail;
        this.securityChecklistComments = securityChecklistComments;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFeatureTitle() { return featureTitle; }
    public void setFeatureTitle(String featureTitle) { this.featureTitle = featureTitle; }

    public String getAuthorEmail() { return authorEmail; }
    public void setAuthorEmail(String authorEmail) { this.authorEmail = authorEmail; }

    public String getReviewerEmail() { return reviewerEmail; }
    public void setReviewerEmail(String reviewerEmail) { this.reviewerEmail = reviewerEmail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSecurityChecklistComments() { return securityChecklistComments; }
    public void setSecurityChecklistComments(String securityChecklistComments) { this.securityChecklistComments = securityChecklistComments; }

    public Boolean getStaticAnalysisPassed() { return staticAnalysisPassed; }
    public void setStaticAnalysisPassed(Boolean staticAnalysisPassed) { this.staticAnalysisPassed = staticAnalysisPassed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
