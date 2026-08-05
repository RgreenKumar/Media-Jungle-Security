package com.VsmartEngine.MediaJungle.accessmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 1: Access Management | Task 2: Access Approval Workflow
// Description: AccessRequest entity for requesting and approving sensitive roles and resource access.
@Entity
@Table(name = "access_requests")
public class AccessRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String requesterEmail;

    @Column(nullable = false)
    private String requestedRole;

    @Column(length = 1000)
    private String reason;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    private String reviewedBy;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime reviewedAt;

    public AccessRequest() {}

    public AccessRequest(String requesterEmail, String requestedRole, String reason) {
        this.requesterEmail = requesterEmail;
        this.requestedRole = requestedRole;
        this.reason = reason;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRequesterEmail() { return requesterEmail; }
    public void setRequesterEmail(String requesterEmail) { this.requesterEmail = requesterEmail; }

    public String getRequestedRole() { return requestedRole; }
    public void setRequestedRole(String requestedRole) { this.requestedRole = requestedRole; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
