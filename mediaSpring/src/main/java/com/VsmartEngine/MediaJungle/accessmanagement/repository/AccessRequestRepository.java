package com.VsmartEngine.MediaJungle.accessmanagement.repository;

import com.VsmartEngine.MediaJungle.accessmanagement.model.AccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ISO 27001 | Module 1: Access Management | Task 2: Access Approval Workflow
// Description: Repository interface for AccessRequest entity operations.
@Repository
public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {
    List<AccessRequest> findByStatus(String status);
    List<AccessRequest> findByRequesterEmail(String requesterEmail);
}
