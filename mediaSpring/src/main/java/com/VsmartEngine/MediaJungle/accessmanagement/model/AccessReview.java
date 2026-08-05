package com.VsmartEngine.MediaJungle.accessmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 1: Access Management | Task 8: Access Reviews
// Description: AccessReview entity recording periodic access permission certification, privilege reviews, and approval/revocation decisions.
@Entity
@Table(name = "access_reviews")
public class AccessReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String targetEmail;

    @Column(nullable = false)
    private String reviewedRole;

    @Column(nullable = false)
    private String reviewerEmail;

    @Column(nullable = false)
    private String decision; // KEPT, REVOKED, MODIFIED

    @Column(length = 1000)
    private String comments;

    private LocalDateTime reviewDate = LocalDateTime.now();

    private LocalDateTime nextReviewDue;

    public AccessReview() {}

    public AccessReview(String targetEmail, String reviewedRole, String reviewerEmail, String decision, String comments) {
        this.targetEmail = targetEmail;
        this.reviewedRole = reviewedRole;
        this.reviewerEmail = reviewerEmail;
        this.decision = decision;
        this.comments = comments;
        this.nextReviewDue = LocalDateTime.now().plusDays(90);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTargetEmail() { return targetEmail; }
    public void setTargetEmail(String targetEmail) { this.targetEmail = targetEmail; }

    public String getReviewedRole() { return reviewedRole; }
    public void setReviewedRole(String reviewedRole) { this.reviewedRole = reviewedRole; }

    public String getReviewerEmail() { return reviewerEmail; }
    public void setReviewerEmail(String reviewerEmail) { this.reviewerEmail = reviewerEmail; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }

    public LocalDateTime getNextReviewDue() { return nextReviewDue; }
    public void setNextReviewDue(LocalDateTime nextReviewDue) { this.nextReviewDue = nextReviewDue; }
}
