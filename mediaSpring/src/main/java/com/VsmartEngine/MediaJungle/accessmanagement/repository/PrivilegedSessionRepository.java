package com.VsmartEngine.MediaJungle.accessmanagement.repository;

import com.VsmartEngine.MediaJungle.accessmanagement.model.PrivilegedSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ISO 27001 | Module 1: Access Management | Task 4: Privileged Access Management
// Description: Repository interface for PrivilegedSession monitoring and management.
@Repository
public interface PrivilegedSessionRepository extends JpaRepository<PrivilegedSession, Long> {
    List<PrivilegedSession> findByActive(Boolean active);
    List<PrivilegedSession> findByAdminEmail(String adminEmail);
}
