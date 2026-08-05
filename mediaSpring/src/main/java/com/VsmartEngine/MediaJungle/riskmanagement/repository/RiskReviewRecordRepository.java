package com.VsmartEngine.MediaJungle.riskmanagement.repository;

import com.VsmartEngine.MediaJungle.riskmanagement.model.RiskReviewRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ISO 27001 | Module 4: Risk Management | Task 10: Risk Review Workflow
// Description: Repository interface for RiskReviewRecord management.
@Repository
public interface RiskReviewRecordRepository extends JpaRepository<RiskReviewRecord, Long> {
    List<RiskReviewRecord> findTop50ByOrderByReviewDateDesc();
}
