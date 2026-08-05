package com.VsmartEngine.MediaJungle.auditlogging.repository;

import com.VsmartEngine.MediaJungle.auditlogging.model.SecurityEventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

// ISO 27001 | Module 5: Audit Logging & Monitoring | Task 4: Security Event Logging
// Description: Repository interface for SecurityEventLog management.
@Repository
public interface SecurityEventLogRepository extends JpaRepository<SecurityEventLog, Long> {
    List<SecurityEventLog> findTop50ByOrderByTimestampDesc();
    long countByTimestampAfterAndEventType(LocalDateTime cutoff, String eventType);
    void deleteByTimestampBefore(LocalDateTime cutoff);
}
