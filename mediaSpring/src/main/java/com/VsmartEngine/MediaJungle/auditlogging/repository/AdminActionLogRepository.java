package com.VsmartEngine.MediaJungle.auditlogging.repository;

import com.VsmartEngine.MediaJungle.auditlogging.model.AdminActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

// ISO 27001 | Module 5: Audit Logging & Monitoring | Task 2: Administrative Action Logging
// Description: Repository interface for AdminActionLog management.
@Repository
public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Long> {
    List<AdminActionLog> findTop50ByOrderByTimestampDesc();
    void deleteByTimestampBefore(LocalDateTime cutoff);
}
