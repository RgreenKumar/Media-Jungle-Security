package com.VsmartEngine.MediaJungle.backup.repository;

import com.VsmartEngine.MediaJungle.backup.model.BusinessContinuityItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ISO 27001 | Module 2: Data Backup & Recovery | Task 9: Business Continuity Tracker
// Description: Repository interface for BusinessContinuityItem management.
@Repository
public interface BusinessContinuityItemRepository extends JpaRepository<BusinessContinuityItem, Long> {
    List<BusinessContinuityItem> findByHealthStatus(String healthStatus);
}
