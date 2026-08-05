package com.VsmartEngine.MediaJungle.backup.repository;

import com.VsmartEngine.MediaJungle.backup.model.BackupRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// ISO 27001 | Module 2: Data Backup & Recovery | Task 1 & 10: Backup Management & Retention
// Description: Repository interface for BackupRecord entity management.
@Repository
public interface BackupRecordRepository extends JpaRepository<BackupRecord, Long> {
    List<BackupRecord> findByExpiresAtBefore(LocalDateTime now);
    Optional<BackupRecord> findTopByOrderByCreatedAtDesc();
    List<BackupRecord> findTop50ByOrderByCreatedAtDesc();
}
