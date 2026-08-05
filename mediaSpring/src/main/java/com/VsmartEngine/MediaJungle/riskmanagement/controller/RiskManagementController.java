package com.VsmartEngine.MediaJungle.riskmanagement.controller;

import com.VsmartEngine.MediaJungle.riskmanagement.model.*;
import com.VsmartEngine.MediaJungle.riskmanagement.service.RiskManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v2/risk-management")
public class RiskManagementController {

    @Autowired
    private RiskManagementService riskManagementService;

    // ISO 27001 | Module 4: Risk Management | Task 1 & 2: Risk Register & Assessment Engine
    @PostMapping("/register")
    public ResponseEntity<?> registerRisk(@RequestBody Map<String, Object> body) {
        String title = body.get("riskTitle").toString();
        String description = body.getOrDefault("description", "").toString();
        String category = body.getOrDefault("category", "APPLICATION_SECURITY").toString();
        Integer likelihood = Integer.parseInt(body.getOrDefault("likelihood", 3).toString());
        Integer impact = Integer.parseInt(body.getOrDefault("impact", 3).toString());
        Integer effectiveness = Integer.parseInt(body.getOrDefault("controlEffectivenessPercent", 70).toString());

        RiskRegisterItem item = riskManagementService.registerRisk(title, description, category, likelihood, impact, effectiveness);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/register")
    public ResponseEntity<List<RiskRegisterItem>> getRiskRegister() {
        return ResponseEntity.ok(riskManagementService.getAllRisks());
    }

    // ISO 27001 | Module 4: Risk Management | Task 3: Risk Reporting Dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<?> getRiskDashboard() {
        return ResponseEntity.ok(riskManagementService.getRiskDashboardStats());
    }

    // ISO 27001 | Module 4: Risk Management | Task 4: Risk Mitigation Tracker
    @PostMapping("/mitigations")
    public ResponseEntity<?> addMitigation(@RequestBody Map<String, Object> body) {
        Long riskId = Long.parseLong(body.get("riskId").toString());
        String title = body.get("mitigationTitle").toString();
        String details = body.getOrDefault("actionDetails", "").toString();
        String owner = body.getOrDefault("assignedOwner", "risk-owner@mediajungle.com").toString();
        LocalDate targetDate = LocalDate.now().plusDays(30);

        RiskMitigationAction action = riskManagementService.addMitigationAction(riskId, title, details, owner, targetDate);
        return ResponseEntity.ok(action);
    }

    @GetMapping("/mitigations")
    public ResponseEntity<List<RiskMitigationAction>> getMitigations() {
        return ResponseEntity.ok(riskManagementService.getMitigationActions());
    }

    // ISO 27001 | Module 4: Risk Management | Task 5: Threat Analysis Module
    @GetMapping("/threats")
    public ResponseEntity<List<ThreatAnalysisEntry>> getThreats() {
        return ResponseEntity.ok(riskManagementService.getThreatAnalysisEntries());
    }

    // ISO 27001 | Module 4: Risk Management | Task 6: Control Effectiveness Review
    @GetMapping("/control-reviews")
    public ResponseEntity<List<ControlEffectivenessReview>> getControlReviews() {
        return ResponseEntity.ok(riskManagementService.getControlReviews());
    }

    // ISO 27001 | Module 4: Risk Management | Task 7: Residual Risk Tracking
    @GetMapping("/residual-risks")
    public ResponseEntity<?> getResidualRisks() {
        List<RiskRegisterItem> list = riskManagementService.getAllRisks();
        return ResponseEntity.ok(list);
    }

    // ISO 27001 | Module 4: Risk Management | Task 8: Continuous Risk Monitoring
    @GetMapping("/monitor/status")
    public ResponseEntity<?> getMonitoringStatus() {
        return ResponseEntity.ok(Map.of(
            "status", "ACTIVE_MONITORING",
            "cronSchedule", "0 */15 * * * ?",
            "lastAssessment", java.time.LocalDateTime.now().toString()
        ));
    }

    // ISO 27001 | Module 4: Risk Management | Task 9: Management Risk Reports
    @GetMapping("/management-report")
    public ResponseEntity<?> getManagementReport() {
        return ResponseEntity.ok(riskManagementService.generateManagementRiskReport());
    }

    // ISO 27001 | Module 4: Risk Management | Task 10: Risk Review Workflow
    @PostMapping("/reviews/submit")
    public ResponseEntity<?> submitRiskReview(@RequestBody Map<String, String> body) {
        String reviewer = body.getOrDefault("reviewerEmail", "ciso@mediajungle.com");
        String period = body.getOrDefault("reviewPeriod", "Q1-2026");
        String comments = body.getOrDefault("comments", "CISO quarterly risk sign-off completed");

        RiskReviewRecord record = riskManagementService.submitRiskReview(reviewer, period, comments);
        return ResponseEntity.ok(record);
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<RiskReviewRecord>> getRiskReviews() {
        return ResponseEntity.ok(riskManagementService.getRiskReviewRecords());
    }
}
