package com.VsmartEngine.MediaJungle.security.standards;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class SecurityStandardsTest {

    private SecurityStandardsService service;
    private SecurityStandardsController controller;

    @BeforeEach
    void setUp() {
        service = new SecurityStandardsService();
        controller = new SecurityStandardsController(service);
    }

    @Test
    void testTotalSecurityStandardsCount() {
        List<SecurityStandard> all = service.getAllStandards();
        assertNotNull(all);
        assertEquals(20, all.size(), "Should have exactly 20 security standards (10 ISO + 10 SOC2)");
    }

    @Test
    void testIso27001Standards() {
        List<SecurityStandard> isoList = service.getStandardsByFramework("ISO 27001");
        assertNotNull(isoList);
        assertEquals(10, isoList.size(), "Should have exactly 10 ISO 27001 standards");
        assertTrue(isoList.stream().allMatch(s -> "ISO 27001".equals(s.getFramework())));
    }

    @Test
    void testSoc2Standards() {
        List<SecurityStandard> soc2List = service.getStandardsByFramework("SOC 2");
        assertNotNull(soc2List);
        assertEquals(10, soc2List.size(), "Should have exactly 10 SOC 2 standards");
        assertTrue(soc2List.stream().allMatch(s -> "SOC 2".equals(s.getFramework())));
    }

    @Test
    void testGetStandardByCode() {
        Optional<SecurityStandard> isoControl = service.getStandardByCode("A.5.1");
        assertTrue(isoControl.isPresent());
        assertEquals("Information Security Policy & Governance", isoControl.get().getName());

        Optional<SecurityStandard> soc2Control = service.getStandardByCode("CC6.1");
        assertTrue(soc2Control.isPresent());
        assertEquals("Logical Access Controls & Authentication", soc2Control.get().getName());
    }

    @Test
    void testComplianceSummary() {
        Map<String, Object> summary = service.getComplianceSummary();
        assertNotNull(summary);
        assertEquals(20L, summary.get("totalStandards"));
        assertEquals(10L, summary.get("iso27001Count"));
        assertEquals(10L, summary.get("soc2Count"));
        assertEquals(20L, summary.get("completedCount"));
        assertEquals(100.0, summary.get("compliancePercentage"));
        assertEquals("COMPLIANT", summary.get("status"));
    }

    @Test
    void testControllerEndpoints() {
        ResponseEntity<List<SecurityStandard>> allResponse = controller.getAllStandards();
        assertEquals(200, allResponse.getStatusCode().value());
        assertNotNull(allResponse.getBody());
        assertEquals(20, allResponse.getBody().size());

        ResponseEntity<List<SecurityStandard>> isoResponse = controller.getIso27001Standards();
        assertEquals(200, isoResponse.getStatusCode().value());
        assertNotNull(isoResponse.getBody());
        assertEquals(10, isoResponse.getBody().size());

        ResponseEntity<List<SecurityStandard>> soc2Response = controller.getSoc2Standards();
        assertEquals(200, soc2Response.getStatusCode().value());
        assertNotNull(soc2Response.getBody());
        assertEquals(10, soc2Response.getBody().size());

        ResponseEntity<Map<String, Object>> summaryResponse = controller.getComplianceSummary();
        assertEquals(200, summaryResponse.getStatusCode().value());
        assertNotNull(summaryResponse.getBody());
        assertEquals("COMPLIANT", summaryResponse.getBody().get("status"));
    }
}
