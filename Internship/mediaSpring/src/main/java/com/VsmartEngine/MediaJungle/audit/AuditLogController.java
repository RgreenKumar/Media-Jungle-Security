// ===========================================
// Internship Security Enhancement
// Feature : Audit Logging
// ISO27001 Control : Logging & Monitoring
// ===========================================
package com.VsmartEngine.MediaJungle.audit;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// ISO27001 Audit Trail: Admin API for viewing, sorting, and filtering audit logs
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    // Constructor Injection
    public AuditLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // ISO27001 Audit Trail: Admin-only API endpoint for Audit Logs with Pagination, Sorting, and Filtering
    // Only users with ADMIN role can access. Returns HTTP 403 Forbidden for non-ADMINs.
    @GetMapping("/auditlogs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLog>> getAuditLogs(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sortBy", defaultValue = "timestamp") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir
    ) {
        // Configure sorting (default newest first)
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        // Build dynamic specification based on filtering criteria
        Specification<AuditLog> spec = AuditLogSpecification.filterAuditLogs(username, role, action, status, startDate, endDate);

        // Fetch paginated and filtered audit logs
        Page<AuditLog> auditLogs = auditLogRepository.findAll(spec, pageable);

        return ResponseEntity.ok(auditLogs);
    }
}
