package com.VsmartEngine.MediaJungle.accessmanagement.repository;

import com.VsmartEngine.MediaJungle.accessmanagement.model.AccessAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ISO 27001 | Module 1: Access Management | Task 10: Access Audit Module
// Description: Repository interface for system access audit log storage and retrieval.
@Repository
public interface AccessAuditLogRepository extends JpaRepository<AccessAuditLog, Long> {
    List<AccessAuditLog> findTop100ByOrderByTimestampDesc();
    List<AccessAuditLog> findByUsernameOrderByTimestampDesc(String username);
    List<AccessAuditLog> findByActionOrderByTimestampDesc(String action);
}
