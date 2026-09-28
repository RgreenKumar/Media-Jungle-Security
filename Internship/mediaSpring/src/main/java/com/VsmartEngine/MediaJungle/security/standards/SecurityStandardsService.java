// ===========================================
// Internship Security Enhancement
// Feature : Security Standards Registry Service
// Standards : ISO27001 & SOC 2 Compliance
// ===========================================
package com.VsmartEngine.MediaJungle.security.standards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class SecurityStandardsService {

    private final List<SecurityStandard> standards = new ArrayList<>();

    public SecurityStandardsService() {
        initializeStandards();
    }

    private void initializeStandards() {
        // =========================================================================
        // 10 ISO/IEC 27001 Information Security Management Standards
        // =========================================================================
        standards.add(new SecurityStandard(
            1L,
            "ISO 27001",
            "A.5.1",
            "Information Security Policy & Governance",
            "Compliance 1: Information Security Policies",
            "Policies are formally established, reviewed, and published to guide information security practices across all services.",
            "SecurityConfig.java, documented project security controls and centralized policies",
            "Completed"
        ));

        standards.add(new SecurityStandard(
            2L,
            "ISO 27001",
            "A.9.1 / A.5.15",
            "Access Control Policy & Enforcement",
            "Compliance 2: Access Control",
            "Access to systems, networks, and services is restricted according to business requirements and user privileges.",
            "Role-Based Access Control (RBAC) enforced with Spring Security @EnableMethodSecurity and @PreAuthorize",
            "Completed"
        ));

        standards.add(new SecurityStandard(
            3L,
            "ISO 27001",
            "A.9.2 / A.5.18",
            "User Registration & Access Rights Management",
            "Compliance 2: Access Control",
            "Full lifecycle management of user registration, authentication, credential provisioning, and role assignment.",
            "UserRegisterController.java, UserRegister.java, and UserRole.java (ADMIN, USER) lifecycle management",
            "Completed"
        ));

        standards.add(new SecurityStandard(
            4L,
            "ISO 27001",
            "A.9.4 / A.5.17",
            "Secret Authentication & Account Lockout",
            "Compliance 2: Access Control",
            "Password complexity rules, defense against brute force attacks, and automated lockout of compromised accounts.",
            "Brute force defense (security.max-login-attempts=5, 15-minute lockout) implemented in UserRegister.java & UserRegisterController.java",
            "Completed"
        ));

        standards.add(new SecurityStandard(
            5L,
            "ISO 27001",
            "A.10.1 / A.8.24",
            "Cryptographic Controls & Password Hashing",
            "Compliance 3: Cryptography",
            "Cryptographic safeguards are implemented to protect confidentiality, integrity, and authenticity of sensitive credentials and data.",
            "BCrypt password hashing for stored credentials; HMAC-SHA256 signature algorithm for JWT security tokens in JwtUtil.java",
            "Completed"
        ));

        standards.add(new SecurityStandard(
            6L,
            "ISO 27001",
            "A.10.2 / A.8.24",
            "Key Management & Token Security",
            "Compliance 3: Cryptography",
            "Cryptographic keys and security tokens are generated, stored securely, and validated across all API requests.",
            "Stateless JWT token generation and validation filter (JwtAuthenticationFilter.java) with strong 256-bit signing key",
            "Completed"
        ));

        standards.add(new SecurityStandard(
            7L,
            "ISO 27001",
            "A.12.1 / A.8.9",
            "Secure Configuration & HTTP Security Headers",
            "Compliance 5/6: Operations & Communications Security",
            "Hardened server and browser configurations defending against clickjacking, MIME-sniffing, XSS, and unauthorized framing.",
            "OWASP Secure Headers configured in SecurityConfig.java: Frame-Options (DENY), CSP, Content-Type-Options, HSTS, Permissions-Policy",
            "Completed"
        ));

        standards.add(new SecurityStandard(
            8L,
            "ISO 27001",
            "A.12.4 / A.8.15",
            "Centralized Security Logging & Event Review",
            "Compliance 5: Operations Security",
            "System events, authentication attempts, and administrative actions are logged, timestamped, and audited.",
            "Persistent audit logging in AuditLog.java, AuditLogService.java, AuditLogRepository.java, and method logging via LoggingAspect.java",
            "Completed"
        ));

        standards.add(new SecurityStandard(
            9L,
            "ISO 27001",
            "A.12.6 / A.8.7",
            "Malware Protection & Secure File Handling",
            "Compliance 5/7: Operations Security & System Maintenance",
            "Protection against malicious uploads through strict validation of file formats, content verification, and size caps.",
            "FileValidationService.java & FileMetadata.java: MIME type verification, extension whitelisting, and size threshold enforcement",
            "Completed"
        ));

        standards.add(new SecurityStandard(
            10L,
            "ISO 27001",
            "A.14.2 / A.8.28",
            "Secure Software Development & Error Masking",
            "Compliance 7: SSDLC",
            "Secure coding practices ensuring user input validation and prevention of sensitive information or stack trace leaks.",
            "Centralized GlobalExceptionHandler.java, input validation DTOs, and exception masking to prevent information leakage",
            "Completed"
        ));

        // =========================================================================
        // 10 SOC 2 Trust Services Criteria (TSC) Standards
        // =========================================================================
        standards.add(new SecurityStandard(
            11L,
            "SOC 2",
            "CC6.1",
            "Logical Access Controls & Authentication",
            "Security",
            "The entity restricts logical access to systems, applications, and protected media data to authorized users and processes.",
            "Stateless Bearer token verification via JwtAuthenticationFilter.java and role authorization on API endpoints",
            "Completed"
        ));

        standards.add(new SecurityStandard(
            12L,
            "SOC 2",
            "CC6.2",
            "User Registration & Identity Verification",
            "Access Control",
            "New internal and external user accounts are registered, verified, and authorized before credentials are granted.",
            "Dual-step registration flow with email OTP verification (VerificationController.java, EmailService.java) and UserRegisterController.java",
            "Completed"
        ));

        standards.add(new SecurityStandard(
            13L,
            "SOC 2",
            "CC6.3",
            "Access Revocation & Account Protection",
            "Access Control",
            "Logical access rights are modified, locked, or revoked when security thresholds or violations are triggered.",
            "Automated account lockout after repeated failed login attempts, tracked via failedLoginAttempts and lockTime in UserRegister.java",
            "Completed"
        ));

        standards.add(new SecurityStandard(
            14L,
            "SOC 2",
            "CC6.6",
            "Perimeter Defense, Network Boundaries & CORS",
            "Security / Access",
            "Logical boundary protection to control ingress/egress network communications and prevent unauthorized cross-origin requests.",
            "Granular Cross-Origin Resource Sharing (CORS) rules in SecurityConfig.java & GlobalCorsConfig.java restricting untrusted origins",
            "Completed"
        ));

        standards.add(new SecurityStandard(
            15L,
            "SOC 2",
            "CC6.7",
            "Data Transmission Security & Cryptographic Transit",
            "Data Protection",
            "Data transmitted across public and internal networks is encrypted using approved protocols to prevent eavesdropping.",
            "HTTP Strict Transport Security (HSTS) with max-age=31536000; includeSubDomains; preload and TLS enforcement in SecurityConfig.java",
            "Completed"
        ));

        standards.add(new SecurityStandard(
            16L,
            "SOC 2",
            "CC6.8",
            "Malicious Code Prevention & Ingestion Checks",
            "Data Protection",
            "Measures prevent or detect the upload or execution of unauthorized or malicious files into the storage environment.",
            "Multipart upload validation in FileValidationService.java: maximum size enforcement (10MB image, 50MB audio, 500MB video) & MIME checking",
            "Completed"
        ));

        standards.add(new SecurityStandard(
            17L,
            "SOC 2",
            "CC7.1",
            "Vulnerability Monitoring & Threat Detection",
            "Incident Response",
            "Systems are continuously monitored to identify vulnerabilities, anomalous behaviors, and security threats.",
            "Aspect-Oriented Logging (LoggingAspect.java) intercepting controller execution, logging unauthorized access, and method trace monitoring",
            "Completed"
        ));

        standards.add(new SecurityStandard(
            18L,
            "SOC 2",
            "CC7.2",
            "Security Event Monitoring & Audit Trail",
            "Monitoring & Logging",
            "Audit logs are maintained for security events, authentication attempts, user registrations, and administrative tasks.",
            "AuditLogController.java & AuditLogService.java: tracking username, client IP address, HTTP method, URL, status, and event description",
            "Completed"
        ));

        standards.add(new SecurityStandard(
            19L,
            "SOC 2",
            "CC7.3",
            "Incident Response & Access Denial Handling",
            "Incident Response",
            "Security incidents, access violations, and anomalous requests are captured, mitigated, and responded to appropriately.",
            "CustomAccessDeniedHandler.java intercepting HTTP 403 access violations, returning structured JSON error payloads and auditing rejections",
            "Completed"
        ));

        standards.add(new SecurityStandard(
            20L,
            "SOC 2",
            "CC8.1",
            "Change Management, Build & System Operations",
            "System Operations",
            "Software changes, database schema evolutions, and system updates are authorized, tracked, tested, and systematically built.",
            "Maven build lifecycle management (pom.xml), JPA DDL evolution controls (spring.jpa.hibernate.ddl-auto=update), and Git tracking",
            "Completed"
        ));
    }

    public List<SecurityStandard> getAllStandards() {
        return Collections.unmodifiableList(standards);
    }

    public List<SecurityStandard> getStandardsByFramework(String framework) {
        return standards.stream()
            .filter(s -> s.getFramework().equalsIgnoreCase(framework.replace("-", " ")))
            .collect(Collectors.toList());
    }

    public Optional<SecurityStandard> getStandardByCode(String code) {
        return standards.stream()
            .filter(s -> s.getCode().equalsIgnoreCase(code))
            .findFirst();
    }

    public Map<String, Object> getComplianceSummary() {
        Map<String, Object> summary = new HashMap<>();
        long total = standards.size();
        long isoCount = standards.stream().filter(s -> "ISO 27001".equalsIgnoreCase(s.getFramework())).count();
        long soc2Count = standards.stream().filter(s -> "SOC 2".equalsIgnoreCase(s.getFramework())).count();
        long completedCount = standards.stream().filter(s -> "Completed".equalsIgnoreCase(s.getStatus())).count();

        summary.put("totalStandards", total);
        summary.put("iso27001Count", isoCount);
        summary.put("soc2Count", soc2Count);
        summary.put("completedCount", completedCount);
        summary.put("compliancePercentage", total > 0 ? (completedCount * 100.0 / total) : 0.0);
        summary.put("status", "COMPLIANT");
        summary.put("frameworksSupported", List.of("ISO 27001", "SOC 2"));

        return summary;
    }
}
