package com.VsmartEngine.MediaJungle.auditlogging.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 5: Audit Logging & Monitoring | Task 10: Audit Review Workflow
// Description: AuditReviewRecord entity tracking periodic auditor sign-offs on system audit records and compliance logs.
@Entity
@Table(name = "audit_review_records")
public class AuditReviewRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String auditorEmail;

    @Column(nullable = false)
    private String auditScope; // FULL_SYSTEM_LOGS, ADMIN_ACTIONS, ACCESS_CONTROL_LOGS

    private Integer totalLogsReviewed;

    @Column(length = 1000)
    private String reviewFindings;

    private LocalDateTime reviewDate = LocalDateTime.now();

    private LocalDateTime nextReviewDue;

    public AuditReviewRecord() {}

    public AuditReviewRecord(String auditorEmail, String auditScope, Integer totalLogsReviewed, String reviewFindings) {
        this.auditorEmail = auditorEmail;
        this.auditScope = auditScope;
        this.totalLogsReviewed = totalLogsReviewed;
        this.reviewFindings = reviewFindings;
        this.nextReviewDue = LocalDateTime.now().plusDays(90);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAuditorEmail() { return auditorEmail; }
    public void setAuditorEmail(String auditorEmail) { this.auditorEmail = auditorEmail; }

    public String getAuditScope() { return auditScope; }
    public void setAuditScope(String auditScope) { this.auditScope = auditScope; }

    public Integer getTotalLogsReviewed() { return totalLogsReviewed; }
    public void setTotalLogsReviewed(Integer totalLogsReviewed) { this.totalLogsReviewed = totalLogsReviewed; }

    public String getReviewFindings() { return reviewFindings; }
    public void setReviewFindings(String reviewFindings) { this.reviewFindings = reviewFindings; }

    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }

    public LocalDateTime getNextReviewDue() { return nextReviewDue; }
    public void setNextReviewDue(LocalDateTime nextReviewDue) { this.nextReviewDue = nextReviewDue; }
}
