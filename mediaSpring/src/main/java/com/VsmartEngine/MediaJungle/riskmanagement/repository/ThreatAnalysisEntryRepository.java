package com.VsmartEngine.MediaJungle.riskmanagement.repository;

import com.VsmartEngine.MediaJungle.riskmanagement.model.ThreatAnalysisEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ISO 27001 | Module 4: Risk Management | Task 5: Threat Analysis Module
// Description: Repository interface for ThreatAnalysisEntry management.
@Repository
public interface ThreatAnalysisEntryRepository extends JpaRepository<ThreatAnalysisEntry, Long> {
    List<ThreatAnalysisEntry> findByStrideCategory(String strideCategory);
}
