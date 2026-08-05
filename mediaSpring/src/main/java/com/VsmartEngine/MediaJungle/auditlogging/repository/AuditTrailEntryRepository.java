package com.VsmartEngine.MediaJungle.auditlogging.repository;

import com.VsmartEngine.MediaJungle.auditlogging.model.AuditTrailEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

// ISO 27001 | Module 5: Audit Logging & Monitoring | Task 3: Audit Trail Repository
// Description: Repository interface for AuditTrailEntry management.
@Repository
public interface AuditTrailEntryRepository extends JpaRepository<AuditTrailEntry, Long> {
    List<AuditTrailEntry> findTop50ByOrderByTimestampDesc();
    void deleteByTimestampBefore(LocalDateTime cutoff);
}
