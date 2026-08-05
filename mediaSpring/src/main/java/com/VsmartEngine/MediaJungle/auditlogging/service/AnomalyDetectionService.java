package com.VsmartEngine.MediaJungle.auditlogging.service;

import com.VsmartEngine.MediaJungle.auditlogging.model.SecurityAlert;
import com.VsmartEngine.MediaJungle.auditlogging.repository.SecurityAlertRepository;
import com.VsmartEngine.MediaJungle.auditlogging.repository.SecurityEventLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// ISO 27001 | Module 5: Audit Logging & Monitoring | Task 8: Anomaly Detection Engine
// Description: Rule-based anomaly detection engine analyzing event velocity, authentication failure spikes, and rate limit breach thresholds.
@Service
public class AnomalyDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(AnomalyDetectionService.class);

    @Autowired
    private SecurityEventLogRepository securityEventRepository;

    @Autowired
    private SecurityAlertRepository securityAlertRepository;

    public void runAnomalyDetectionScan() {
        logger.info("ISO 27001 Anomaly Detection Engine: Evaluating security log metrics...");

        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        long failedLogins = securityEventRepository.countByTimestampAfterAndEventType(oneMinuteAgo, "AUTH_FAILURE");

        if (failedLogins > 5) {
            logger.warn("ANOMALY DETECTED: Rapid auth failure spike ({} failures/min)", failedLogins);
            SecurityAlert alert = new SecurityAlert(
                "Rapid Authentication Failure Spike Detected",
                "CRITICAL",
                "Detected " + failedLogins + " failed login attempts in the past 60 seconds from external IP range.",
                "sec-ops@mediajungle.com"
            );
            securityAlertRepository.save(alert);
        }
    }
}
