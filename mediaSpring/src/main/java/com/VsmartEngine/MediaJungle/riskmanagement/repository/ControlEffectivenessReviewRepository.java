package com.VsmartEngine.MediaJungle.riskmanagement.repository;

import com.VsmartEngine.MediaJungle.riskmanagement.model.ControlEffectivenessReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ISO 27001 | Module 4: Risk Management | Task 6: Control Effectiveness Review
// Description: Repository interface for ControlEffectivenessReview management.
@Repository
public interface ControlEffectivenessReviewRepository extends JpaRepository<ControlEffectivenessReview, Long> {
    List<ControlEffectivenessReview> findTop50ByOrderByTestDateDesc();
}
