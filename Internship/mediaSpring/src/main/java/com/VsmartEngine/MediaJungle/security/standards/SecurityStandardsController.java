// ===========================================
// Internship Security Enhancement
// Feature : Security Standards REST Controller
// Standards : ISO27001 & SOC 2 Compliance API
// ===========================================
package com.VsmartEngine.MediaJungle.security.standards;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v2/security-standards")
public class SecurityStandardsController {

    private final SecurityStandardsService securityStandardsService;

    public SecurityStandardsController(SecurityStandardsService securityStandardsService) {
        this.securityStandardsService = securityStandardsService;
    }

    // Get all 20 security standards (10 ISO27001 + 10 SOC2)
    @GetMapping
    public ResponseEntity<List<SecurityStandard>> getAllStandards() {
        return ResponseEntity.ok(securityStandardsService.getAllStandards());
    }

    // Get 10 ISO 27001 standards
    @GetMapping("/iso27001")
    public ResponseEntity<List<SecurityStandard>> getIso27001Standards() {
        return ResponseEntity.ok(securityStandardsService.getStandardsByFramework("ISO 27001"));
    }

    // Get 10 SOC 2 standards
    @GetMapping("/soc2")
    public ResponseEntity<List<SecurityStandard>> getSoc2Standards() {
        return ResponseEntity.ok(securityStandardsService.getStandardsByFramework("SOC 2"));
    }

    // Get compliance summary statistics
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getComplianceSummary() {
        return ResponseEntity.ok(securityStandardsService.getComplianceSummary());
    }

    // Get single standard by control code (e.g. A.5.1 or CC6.1)
    @GetMapping("/code/{code}")
    public ResponseEntity<SecurityStandard> getStandardByCode(@PathVariable String code) {
        return securityStandardsService.getStandardByCode(code)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
