package com.VsmartEngine.MediaJungle.auditlogging.repository;

import com.VsmartEngine.MediaJungle.auditlogging.model.SecurityAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ISO 27001 | Module 5: Audit Logging & Monitoring | Task 6: Alert Management System
// Description: Repository interface for SecurityAlert management.
@Repository
public interface SecurityAlertRepository extends JpaRepository<SecurityAlert, Long> {
    List<SecurityAlert> findByStatus(String status);
}
