package com.VsmartEngine.MediaJungle.riskmanagement.repository;

import com.VsmartEngine.MediaJungle.riskmanagement.model.RiskMitigationAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ISO 27001 | Module 4: Risk Management | Task 4: Risk Mitigation Tracker
// Description: Repository interface for RiskMitigationAction management.
@Repository
public interface RiskMitigationActionRepository extends JpaRepository<RiskMitigationAction, Long> {
    List<RiskMitigationAction> findByRiskId(Long riskId);
    List<RiskMitigationAction> findByStatus(String status);
}
