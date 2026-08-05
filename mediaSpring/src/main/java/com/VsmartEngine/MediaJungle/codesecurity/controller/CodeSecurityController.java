package com.VsmartEngine.MediaJungle.codesecurity.controller;

import com.VsmartEngine.MediaJungle.codesecurity.model.*;
import com.VsmartEngine.MediaJungle.codesecurity.service.CodeSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v2/code-security")
public class CodeSecurityController {

    @Autowired
    private CodeSecurityService codeSecurityService;

    // ISO 27001 | Module 3: Code Level Security | Task 1: Secure Coding Standards
    @GetMapping("/standards")
    public ResponseEntity<?> getStandards() {
        return ResponseEntity.ok(codeSecurityService.getSecureCodingStandards());
    }

    // ISO 27001 | Module 3: Code Level Security | Task 2: Input Validation Framework
    @PostMapping("/validate-input")
    public ResponseEntity<?> validateInput(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(codeSecurityService.validateInputText(body.get("input")));
    }

    // ISO 27001 | Module 3: Code Level Security | Task 3: SQL Injection Protection
    @PostMapping("/sqli-check")
    public ResponseEntity<?> checkSqli(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(codeSecurityService.inspectSqlInjection(body.get("input")));
    }

    // ISO 27001 | Module 3: Code Level Security | Task 4: XSS Protection
    @PostMapping("/sanitize-xss")
    public ResponseEntity<?> sanitizeXss(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(codeSecurityService.sanitizeXssContent(body.get("input")));
    }

    // ISO 27001 | Module 3: Code Level Security | Task 5: CSRF Protection
    @GetMapping("/csrf-token")
    public ResponseEntity<?> getCsrfToken(@RequestParam(defaultValue = "session_default") String sessionId) {
        String token = codeSecurityService.getCsrfToken(sessionId);
        return ResponseEntity.ok(Map.of("sessionId", sessionId, "csrfToken", token, "headerName", "X-CSRF-TOKEN"));
    }

    // ISO 27001 | Module 3: Code Level Security | Task 6: Secure API Development
    @GetMapping("/api-security/metrics")
    public ResponseEntity<?> getApiSecurityMetrics() {
        return ResponseEntity.ok(codeSecurityService.getApiSecurityMetrics());
    }

    // ISO 27001 | Module 3: Code Level Security | Task 7: Dependency Management
    @GetMapping("/dependencies")
    public ResponseEntity<List<DependencyVulnerability>> getDependencies() {
        return ResponseEntity.ok(codeSecurityService.scanDependencies());
    }

    // ISO 27001 | Module 3: Code Level Security | Task 8: Code Review Workflow
    @PostMapping("/code-reviews/submit")
    public ResponseEntity<?> submitCodeReview(@RequestBody Map<String, String> body) {
        CodeReviewEntry entry = codeSecurityService.submitCodeReview(
            body.get("featureTitle"),
            body.get("authorEmail"),
            body.getOrDefault("reviewerEmail", "reviewer@mediajungle.com"),
            body.get("comments")
        );
        return ResponseEntity.ok(entry);
    }

    @PutMapping("/code-reviews/{id}/review")
    public ResponseEntity<?> approveCodeReview(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(codeSecurityService.approveCodeReview(id, body.get("status")));
    }

    @GetMapping("/code-reviews")
    public ResponseEntity<List<CodeReviewEntry>> getCodeReviews() {
        return ResponseEntity.ok(codeSecurityService.getAllCodeReviews());
    }

    // ISO 27001 | Module 3: Code Level Security | Task 9: Security Testing
    @PostMapping("/security-testing/run")
    public ResponseEntity<?> runSecurityScan(@RequestBody(required = false) Map<String, String> body) {
        String testType = (body != null && body.containsKey("testType")) ? body.get("testType") : "DAST_API_SCAN";
        return ResponseEntity.ok(codeSecurityService.runAutomatedSecurityScan(testType));
    }

    @GetMapping("/security-testing/runs")
    public ResponseEntity<List<SecurityTestRun>> getSecurityTestRuns() {
        return ResponseEntity.ok(codeSecurityService.getSecurityTestRuns());
    }

    // ISO 27001 | Module 3: Code Level Security | Task 10: Vulnerability Management Portal
    @GetMapping("/vulnerabilities")
    public ResponseEntity<List<VulnerabilityItem>> getVulnerabilities() {
        return ResponseEntity.ok(codeSecurityService.getVulnerabilities());
    }

    @PutMapping("/vulnerabilities/{id}/status")
    public ResponseEntity<?> updateVulnerabilityStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(codeSecurityService.updateVulnerabilityStatus(id, body.get("status")));
    }
}
