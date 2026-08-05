package com.VsmartEngine.MediaJungle.backup.repository;

import com.VsmartEngine.MediaJungle.backup.model.RecoveryTestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ISO 27001 | Module 2: Data Backup & Recovery | Task 6: Recovery Testing
// Description: Repository interface for RecoveryTestLog management.
@Repository
public interface RecoveryTestLogRepository extends JpaRepository<RecoveryTestLog, Long> {
    List<RecoveryTestLog> findTop50ByOrderByTestTimeDesc();
}
