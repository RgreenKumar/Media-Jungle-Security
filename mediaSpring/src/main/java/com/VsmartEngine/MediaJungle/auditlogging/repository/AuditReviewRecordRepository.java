package com.VsmartEngine.MediaJungle.auditlogging.repository;

import com.VsmartEngine.MediaJungle.auditlogging.model.AuditReviewRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ISO 27001 | Module 5: Audit Logging & Monitoring | Task 10: Audit Review Workflow
// Description: Repository interface for AuditReviewRecord management.
@Repository
public interface AuditReviewRecordRepository extends JpaRepository<AuditReviewRecord, Long> {
    List<AuditReviewRecord> findTop50ByOrderByReviewDateDesc();
}
