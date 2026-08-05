package com.VsmartEngine.MediaJungle.accessmanagement.repository;

import com.VsmartEngine.MediaJungle.accessmanagement.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// ISO 27001 | Module 1: Access Management | Task 5: Session Monitoring
// Description: Repository interface for UserSession tracking and forced session termination.
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    List<UserSession> findByStatus(String status);
    List<UserSession> findByUserEmailAndStatus(String userEmail, String status);
    Optional<UserSession> findByTokenHash(String tokenHash);
}
