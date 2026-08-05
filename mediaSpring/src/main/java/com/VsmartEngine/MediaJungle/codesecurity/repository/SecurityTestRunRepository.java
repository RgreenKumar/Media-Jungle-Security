package com.VsmartEngine.MediaJungle.codesecurity.repository;

import com.VsmartEngine.MediaJungle.codesecurity.model.SecurityTestRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ISO 27001 | Module 3: Code Level Security | Task 9: Security Testing
// Description: Repository interface for SecurityTestRun management.
@Repository
public interface SecurityTestRunRepository extends JpaRepository<SecurityTestRun, Long> {
    List<SecurityTestRun> findTop50ByOrderByRunTimeDesc();
}
