package com.VsmartEngine.MediaJungle.accessmanagement.repository;

import com.VsmartEngine.MediaJungle.accessmanagement.model.LoginActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ISO 27001 | Module 1: Access Management | Task 9: Login Activity Dashboard
// Description: Repository interface for LoginActivity monitoring and historical reporting.
@Repository
public interface LoginActivityRepository extends JpaRepository<LoginActivity, Long> {
    List<LoginActivity> findByEmailOrderByTimestampDesc(String email);
    List<LoginActivity> findTop100ByOrderByTimestampDesc();
    long countByStatus(String status);
}
