package com.VsmartEngine.MediaJungle.codesecurity.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 3: Code Level Security | Task 9: Security Testing
// Description: SecurityTestRun entity logging automated DAST/SAST security scan executions, vulnerabilities detected count, and report summaries.
@Entity
@Table(name = "security_test_runs")
public class SecurityTestRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String testType; // DAST_API_SCAN, SAST_STATIC_ANALYSIS, DEPENDENCY_AUDIT

    private Integer totalChecksPerformed;

    private Integer vulnerabilitiesFoundCount;

    private Integer criticalCount = 0;

    private Integer highCount = 0;

    @Column(length = 2000)
    private String summaryReport;

    private String status = "PASSED"; // PASSED, WARNING, FAILED

    private LocalDateTime runTime = LocalDateTime.now();

    public SecurityTestRun() {}

    public SecurityTestRun(String testType, Integer totalChecksPerformed, Integer vulnerabilitiesFoundCount, Integer criticalCount, Integer highCount, String summaryReport, String status) {
        this.testType = testType;
        this.totalChecksPerformed = totalChecksPerformed;
        this.vulnerabilitiesFoundCount = vulnerabilitiesFoundCount;
        this.criticalCount = criticalCount;
        this.highCount = highCount;
        this.summaryReport = summaryReport;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }

    public Integer getTotalChecksPerformed() { return totalChecksPerformed; }
    public void setTotalChecksPerformed(Integer totalChecksPerformed) { this.totalChecksPerformed = totalChecksPerformed; }

    public Integer getVulnerabilitiesFoundCount() { return vulnerabilitiesFoundCount; }
    public void setVulnerabilitiesFoundCount(Integer vulnerabilitiesFoundCount) { this.vulnerabilitiesFoundCount = vulnerabilitiesFoundCount; }

    public Integer getCriticalCount() { return criticalCount; }
    public void setCriticalCount(Integer criticalCount) { this.criticalCount = criticalCount; }

    public Integer getHighCount() { return highCount; }
    public void setHighCount(Integer highCount) { this.highCount = highCount; }

    public String getSummaryReport() { return summaryReport; }
    public void setSummaryReport(String summaryReport) { this.summaryReport = summaryReport; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getRunTime() { return runTime; }
    public void setRunTime(LocalDateTime runTime) { this.runTime = runTime; }
}
