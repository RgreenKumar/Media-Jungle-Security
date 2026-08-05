package com.VsmartEngine.MediaJungle.backup.repository;

import com.VsmartEngine.MediaJungle.backup.model.DisasterRecoveryMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// ISO 27001 | Module 2: Data Backup & Recovery | Task 5: Disaster Recovery Dashboard
// Description: Repository interface for DisasterRecoveryMetrics management.
@Repository
public interface DisasterRecoveryMetricsRepository extends JpaRepository<DisasterRecoveryMetrics, Long> {
    Optional<DisasterRecoveryMetrics> findTopByOrderByIdDesc();
}
