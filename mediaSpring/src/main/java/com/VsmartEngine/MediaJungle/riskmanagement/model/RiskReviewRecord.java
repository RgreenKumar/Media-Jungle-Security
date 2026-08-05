package com.VsmartEngine.MediaJungle.riskmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 4: Risk Management | Task 10: Risk Review Workflow
// Description: RiskReviewRecord entity tracking periodic risk re-certification sign-offs by CISO / Risk Officer.
@Entity
@Table(name = "risk_review_records")
public class RiskReviewRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reviewerEmail;

    @Column(nullable = false)
    private String reviewPeriod; // Q1-2026, Q2-2026, ANNUAL-2026

    private Integer totalRisksReviewed;

    private Integer highCriticalCount;

    @Column(length = 1000)
    private String cisoSignoffComments;

    private LocalDateTime reviewDate = LocalDateTime.now();

    private LocalDateTime nextReviewDue;

    public RiskReviewRecord() {}

    public RiskReviewRecord(String reviewerEmail, String reviewPeriod, Integer totalRisksReviewed, Integer highCriticalCount, String cisoSignoffComments) {
        this.reviewerEmail = reviewerEmail;
        this.reviewPeriod = reviewPeriod;
        this.totalRisksReviewed = totalRisksReviewed;
        this.highCriticalCount = highCriticalCount;
        this.cisoSignoffComments = cisoSignoffComments;
        this.nextReviewDue = LocalDateTime.now().plusDays(90);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReviewerEmail() { return reviewerEmail; }
    public void setReviewerEmail(String reviewerEmail) { this.reviewerEmail = reviewerEmail; }

    public String getReviewPeriod() { return reviewPeriod; }
    public void setReviewPeriod(String reviewPeriod) { this.reviewPeriod = reviewPeriod; }

    public Integer getTotalRisksReviewed() { return totalRisksReviewed; }
    public void setTotalRisksReviewed(Integer totalRisksReviewed) { this.totalRisksReviewed = totalRisksReviewed; }

    public Integer getHighCriticalCount() { return highCriticalCount; }
    public void setHighCriticalCount(Integer highCriticalCount) { this.highCriticalCount = highCriticalCount; }

    public String getCisoSignoffComments() { return cisoSignoffComments; }
    public void setCisoSignoffComments(String cisoSignoffComments) { this.cisoSignoffComments = cisoSignoffComments; }

    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }

    public LocalDateTime getNextReviewDue() { return nextReviewDue; }
    public void setNextReviewDue(LocalDateTime nextReviewDue) { this.nextReviewDue = nextReviewDue; }
}
