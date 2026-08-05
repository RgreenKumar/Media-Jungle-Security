package com.VsmartEngine.MediaJungle.auditlogging.repository;

import com.VsmartEngine.MediaJungle.auditlogging.model.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

// ISO 27001 | Module 5: Audit Logging & Monitoring | Task 1: User Activity Logging
// Description: Repository interface for UserActivityLog management.
@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    List<UserActivityLog> findTop50ByOrderByTimestampDesc();
    void deleteByTimestampBefore(LocalDateTime cutoff);
}
