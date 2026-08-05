package com.VsmartEngine.MediaJungle.backup.scheduler;

import com.VsmartEngine.MediaJungle.backup.service.BackupManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// ISO 27001 | Module 2: Data Backup & Recovery | Task 2 & 10: Automated Backup Scheduler & Retention Lifecycle Purging
// Description: Scheduled Spring background component executing daily automated backup jobs and daily retention purging of expired backup files.
@Component
public class BackupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(BackupScheduler.class);

    @Autowired
    private BackupManagementService backupManagementService;

    private boolean schedulerEnabled = true;

    // ISO 27001 | Task 2: Automated Backup Scheduler
    // Runs daily at 02:00 AM (0 0 2 * * ?)
    @Scheduled(cron = "${backup.schedule.cron:0 0 2 * * ?}")
    public void runAutomatedScheduledBackup() {
        if (!schedulerEnabled) {
            logger.info("BackupScheduler is disabled. Skipping automated scheduled backup.");
            return;
        }
        logger.info("ISO 27001 Automated Backup Scheduler: Executing daily scheduled backup...");
        try {
            backupManagementService.createBackup("AUTOMATED_SCHEDULER", 30);
            logger.info("ISO 27001 Automated Scheduled Backup completed successfully.");
        } catch (Exception e) {
            logger.error("ISO 27001 Automated Scheduled Backup failed", e);
        }
    }

    // ISO 27001 | Task 10: Retention Lifecycle Purging
    // Runs daily at 03:00 AM (0 0 3 * * ?)
    @Scheduled(cron = "${backup.retention.cron:0 0 3 * * ?}")
    public void runAutomatedRetentionPurge() {
        logger.info("ISO 27001 Retention Management: Running daily expired backup cleanup...");
        try {
            int purged = backupManagementService.purgeExpiredBackups();
            logger.info("ISO 27001 Retention Management: Cleaned up {} expired backup archives.", purged);
        } catch (Exception e) {
            logger.error("ISO 27001 Retention Management Purge failed", e);
        }
    }

    public boolean isSchedulerEnabled() {
        return schedulerEnabled;
    }

    public void setSchedulerEnabled(boolean schedulerEnabled) {
        this.schedulerEnabled = schedulerEnabled;
    }
}
