package com.VsmartEngine.MediaJungle.riskmanagement.repository;

import com.VsmartEngine.MediaJungle.riskmanagement.model.RiskRegisterItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ISO 27001 | Module 4: Risk Management | Task 1: Risk Register
// Description: Repository interface for RiskRegisterItem management.
@Repository
public interface RiskRegisterItemRepository extends JpaRepository<RiskRegisterItem, Long> {
    List<RiskRegisterItem> findByInherentRiskLevel(String inherentRiskLevel);
    List<RiskRegisterItem> findByStatus(String status);
    long countByInherentRiskLevelIn(List<String> levels);
}
