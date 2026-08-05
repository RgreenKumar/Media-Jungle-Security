package com.VsmartEngine.MediaJungle.codesecurity.repository;

import com.VsmartEngine.MediaJungle.codesecurity.model.CodeReviewEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ISO 27001 | Module 3: Code Level Security | Task 8: Code Review Workflow
// Description: Repository interface for CodeReviewEntry management.
@Repository
public interface CodeReviewEntryRepository extends JpaRepository<CodeReviewEntry, Long> {
    List<CodeReviewEntry> findByStatus(String status);
}
