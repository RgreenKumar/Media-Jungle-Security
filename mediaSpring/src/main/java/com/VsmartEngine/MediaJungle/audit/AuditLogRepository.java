// ===========================================
// Internship Security Enhancement
// Feature : Audit Logging
// ISO27001 Control : Logging & Monitoring
// ===========================================
package com.VsmartEngine.MediaJungle.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

// ISO27001 Audit Trail: AuditLogRepository for database operations and dynamic criteria queries
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {
}
