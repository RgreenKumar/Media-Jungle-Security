package com.VsmartEngine.MediaJungle.gdpr;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.VsmartEngine.MediaJungle.userregister.UserRegister;
import com.VsmartEngine.MediaJungle.userregister.UserRegisterRepository;

/**
 * GDPR-TASK-17: Storage limitation (GDPR Art. 5(1)(e)) - personal data must not be kept longer
 * than necessary. This job runs daily and:
 *   1. Anonymises registration rows that never completed consent (abandoned sign-ups), and
 *   2. Purges GDPR audit-log entries older than the configured retention window.
 * Retention windows are configurable via application.properties (see GDPR-TASK-20).
 */
@Component
public class DataRetentionScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DataRetentionScheduler.class);

    @Autowired
    private UserRegisterRepository userRegisterRepository;

    @Autowired
    private GdprAuditLogRepository auditLogRepository;

    @Autowired
    private GdprService gdprService;

    @Value("${gdpr.retention.unconfirmed-account-days:30}")
    private int unconfirmedAccountRetentionDays;

    @Value("${gdpr.retention.audit-log-days:730}")
    private int auditLogRetentionDays;

    // Runs once a day at 02:00 server time.
    @Scheduled(cron = "0 0 2 * * *")
    public void purgeStaleData() {
        purgeUnconfirmedAccounts();
        purgeOldAuditLogs();
    }

    private void purgeUnconfirmedAccounts() {
        LocalDate cutoff = LocalDate.now().minusDays(unconfirmedAccountRetentionDays);
        List<UserRegister> stale = userRegisterRepository.findByConsentGivenFalseAndDateBefore(cutoff);
        for (UserRegister user : stale) {
            gdprService.eraseUserData(user.getId());
            logger.info("GDPR retention job anonymised abandoned/unconfirmed account id={}", user.getId());
        }
    }

    private void purgeOldAuditLogs() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(auditLogRetentionDays);
        List<GdprAuditLog> stale = auditLogRepository.findByTimestampBefore(cutoff);
        if (!stale.isEmpty()) {
            auditLogRepository.deleteAll(stale);
            logger.info("GDPR retention job purged {} audit-log entries older than {} days.",
                    stale.size(), auditLogRetentionDays);
        }
    }
}
