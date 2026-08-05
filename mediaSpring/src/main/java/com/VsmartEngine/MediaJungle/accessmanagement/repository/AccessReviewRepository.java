package com.VsmartEngine.MediaJungle.accessmanagement.repository;

import com.VsmartEngine.MediaJungle.accessmanagement.model.AccessReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ISO 27001 | Module 1: Access Management | Task 8: Access Reviews
// Description: Repository interface for periodic access privilege reviews and audit records.
@Repository
public interface AccessReviewRepository extends JpaRepository<AccessReview, Long> {
    List<AccessReview> findByTargetEmail(String targetEmail);
    List<AccessReview> findByReviewerEmail(String reviewerEmail);
}
