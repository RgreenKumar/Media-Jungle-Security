package com.VsmartEngine.MediaJungle.riskmanagement.scheduler;

import com.VsmartEngine.MediaJungle.riskmanagement.service.RiskManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// ISO 27001 | Module 4: Risk Management | Task 8: Continuous Risk Monitoring
// Description: Scheduled background monitoring component continuously assessing system security exposure and emerging risks.
@Component
public class RiskMonitoringScheduler {

    private static final Logger logger = LoggerFactory.getLogger(RiskMonitoringScheduler.class);

    @Autowired
    private RiskManagementService riskManagementService;

    // ISO 27001 | Task 8: Continuous Risk Monitoring
    // Runs every 15 minutes (0 */15 * * * ?)
    @Scheduled(cron = "${risk.monitoring.cron:0 */15 * * * ?}")
    public void runContinuousRiskAssessment() {
        logger.info("ISO 27001 Continuous Risk Monitoring: Assessing active risk exposure across OTT infrastructure...");
        try {
            riskManagementService.performContinuousRiskAssessment();
            logger.info("ISO 27001 Continuous Risk Monitoring completed.");
        } catch (Exception e) {
            logger.error("ISO 27001 Risk Monitoring execution failed", e);
        }
    }
}
