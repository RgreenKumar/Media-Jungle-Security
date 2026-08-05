package com.VsmartEngine.MediaJungle.gdpr;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// GDPR-TASK-07: Persistence for the GDPR audit trail (see GdprAuditLog / GDPR-TASK-06).
@Repository
public interface GdprAuditLogRepository extends JpaRepository<GdprAuditLog, Long> {

    List<GdprAuditLog> findByUserIdOrderByTimestampDesc(Long userId);

    // Used by the data-retention job to purge audit records older than the configured
    // retention window (GDPR Art. 5(1)(e) - storage limitation applies to audit data too).
    List<GdprAuditLog> findByTimestampBefore(LocalDateTime cutoff);
}
