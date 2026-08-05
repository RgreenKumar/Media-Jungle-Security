package com.VsmartEngine.MediaJungle.auditlogging.scheduler;

import com.VsmartEngine.MediaJungle.auditlogging.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// ISO 27001 | Module 5: Audit Logging & Monitoring | Task 7: Log Retention Management
// Description: Scheduled log retention manager executing daily retention cleanup of expired audit records past compliance retention threshold (90 days).
@Component
public class AuditLogRetentionScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogRetentionScheduler.class);

    @Autowired
    private UserActivityLogRepository userActivityRepository;

    @Autowired
    private AdminActionLogRepository adminActionRepository;

    @Autowired
    private AuditTrailEntryRepository auditTrailRepository;

    @Autowired
    private SecurityEventLogRepository securityEventRepository;

    // ISO 27001 | Task 7: Log Retention Management - Daily at 02:00 AM (0 0 2 * * ?)
    @Scheduled(cron = "${audit.retention.cron:0 0 2 * * ?}")
    public void purgeExpiredAuditLogs() {
        int retentionDays = 90;
        logger.info("ISO 27001 Log Retention: Purging audit records older than {} days...", retentionDays);
        try {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);
            userActivityRepository.deleteByTimestampBefore(cutoff);
            adminActionRepository.deleteByTimestampBefore(cutoff);
            auditTrailRepository.deleteByTimestampBefore(cutoff);
            securityEventRepository.deleteByTimestampBefore(cutoff);
            logger.info("ISO 27001 Log Retention purge completed successfully.");
        } catch (Exception e) {
            logger.error("Failed to purge expired audit logs", e);
        }
    }
}
