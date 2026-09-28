// ===========================================
// Internship Security Enhancement
// Feature : Audit Logging
// ISO27001 Control : Logging & Monitoring
// ===========================================
package com.VsmartEngine.MediaJungle.audit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

// ISO27001 Audit Trail: Specification builder for dynamic audit log filtering
public class AuditLogSpecification {

    public static Specification<AuditLog> filterAuditLogs(String username, String role, String action,
                                                          String status, LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by Username
            if (username != null && !username.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + username.trim().toLowerCase() + "%"));
            }

            // Filter by Role
            if (role != null && !role.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.upper(root.get("role")), role.trim().toUpperCase()));
            }

            // Filter by Action
            if (action != null && !action.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("action")), "%" + action.trim().toLowerCase() + "%"));
            }

            // Filter by Status
            if (status != null && !status.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.upper(root.get("status")), status.trim().toUpperCase()));
            }

            // Filter by Start Date
            if (startDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), startDateTime));
            }

            // Filter by End Date
            if (endDate != null) {
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), endDateTime));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
