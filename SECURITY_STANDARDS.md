# Media Jungle Security Standards & Compliance Matrix

This document outlines the **20 ISO/IEC 27001:2022** and **20 SOC 2 Trust Services Criteria (TSC)** security standards implemented and maintained within the Media Jungle platform, establishing an enterprise-grade Information Security Management System (ISMS) and Trust Services framework for secure OTT media streaming.

---

## 1. ISO/IEC 27001:2022 Information Security Management Standards

| # | Control ID | Control Standard Name | Compliance Domain | Description & Objectives | Media Jungle Implementation Reference | Status |
|---|---|---|---|---|---|---|
| 1 | **A.5.1** | Information Security Policy & Governance | Organizational | Policies are formally established, reviewed, and published to guide information security practices across services. | Centralized security policies in `SecurityConfig.java`, documented API standards, and configuration policies. | **Completed** |
| 2 | **A.9.1 / A.5.15** | Access Control Policy & Enforcement | Access Control | Access to systems, networks, and services is restricted according to business requirements and user privileges. | Role-Based Access Control (RBAC) enforced with Spring Security `@EnableMethodSecurity` and `@PreAuthorize` guards. | **Completed** |
| 3 | **A.9.2 / A.5.18** | User Registration & Access Rights Management | Access Control | Full lifecycle management of user registration, authentication, credential provisioning, and role assignment. | `UserRegisterController.java`, `UserRegister.java`, and `UserRole.java` (`ADMIN`, `USER`) lifecycle management. | **Completed** |
| 4 | **A.9.4 / A.5.17** | Secret Authentication & Account Lockout | Access Control | Password complexity rules, defense against brute force attacks, and automated lockout of compromised accounts. | Brute force defense (`security.max-login-attempts=5`, 15-minute lockout) implemented in `UserRegister.java` & `UserRegisterController.java`. | **Completed** |
| 5 | **A.10.1 / A.8.24** | Cryptographic Controls & Password Hashing | Cryptography | Cryptographic safeguards are implemented to protect confidentiality, integrity, and authenticity of sensitive credentials and data. | BCrypt password hashing for stored credentials; HMAC-SHA256 signature algorithm for JWT security tokens in `JwtUtil.java`. | **Completed** |
| 6 | **A.10.2 / A.8.24** | Key Management & Token Security | Cryptography | Cryptographic keys and security tokens are generated, stored securely, and validated across all API requests. | Stateless JWT token generation and validation filter (`JwtAuthenticationFilter.java`) with strong 256-bit signing key. | **Completed** |
| 7 | **A.12.1 / A.8.9** | Secure Configuration & HTTP Security Headers | Operations / Comm | Hardened server and browser configurations defending against clickjacking, MIME-sniffing, XSS, and unauthorized framing. | OWASP Secure Headers configured in `SecurityConfig.java`: Frame-Options (DENY), CSP, Content-Type-Options, HSTS, Permissions-Policy. | **Completed** |
| 8 | **A.12.4 / A.8.15** | Centralized Security Logging & Event Review | Operations Security | System events, authentication attempts, and administrative actions are logged, timestamped, and audited. | Persistent audit logging in `AuditLog.java`, `AuditLogService.java`, `AuditLogRepository.java`, and method logging via `LoggingAspect.java`. | **Completed** |
| 9 | **A.12.6 / A.8.7** | Malware Protection & Secure File Handling | System Maintenance | Protection against malicious uploads through strict validation of file formats, content verification, and size caps. | `FileValidationService.java` & `FileMetadata.java`: MIME type verification, extension whitelisting, and size threshold enforcement. | **Completed** |
| 10 | **A.14.2 / A.8.28** | Secure Software Development & Error Masking | SSDLC | Secure coding practices ensuring user input validation and prevention of sensitive information or stack trace leaks. | Centralized `GlobalExceptionHandler.java`, input validation DTOs, and exception masking to prevent information leakage. | **Completed** |
| 11 | **A.5.7** | Threat Intelligence & Attack Vector Profiling | Organizational | Proactive monitoring and defense against streaming piracy, credential stuffing, and dependency vulnerabilities. | Threat modeling guidelines in `docs/INCIDENT_RESPONSE.md` and NVD alert monitoring for Spring/FFmpeg. | **Completed** |
| 12 | **A.5.10 / A.8.10** | Information Deletion & Data Sanitization | Technological | Systematic deletion and data sanitization for user data upon request and orphaned media asset cleanup. | User deletion workflows, database cascading, and automated asset pruning documented in `docs/DATA_PROTECTION_AND_PRIVACY_POLICY.md`. | **Completed** |
| 13 | **A.5.19 / A.5.20** | Supplier Relationships & Third-Party Security | Organizational | Governance and risk controls governing external APIs (Google OAuth, SMTP providers, payment processors). | Third-party vendor policy in `docs/THIRD_PARTY_VENDOR_RISK_POLICY.md`, credential isolation, and TLS 1.3 requirements. | **Completed** |
| 14 | **A.5.23** | Cloud Media & Digital Asset Storage Security | Organizational | Segregation of public assets and private premium media streams with restricted access permissions. | Asset segregation (`app.storage.public-dir` vs `app.storage.private-dir`) and token-gated DASH playback. | **Completed** |
| 15 | **A.5.29 / A.8.13** | Business Continuity & Streaming Redundancy | Operations Security | Continuity planning, off-site database backups, and media library redundancy. | Database automated backup scripts in `docs/BACKUP_AND_RECOVERY.md` with RTO $\le 4$h and RPO $\le 1$h targets. | **Completed** |
| 16 | **A.5.34 / A.8.11** | Privacy, PII Protection & Data Masking | Compliance / Legal | Protection of Personally Identifiable Information and masking of sensitive credentials in logs and APIs. | Write-only password serialization (`@JsonProperty`), log masking in `LoggingAspect.java`, and minimal PII ingestion. | **Completed** |
| 17 | **A.8.8** | Technical Vulnerability Management & Patching | Technological | Continuous scanning, dependency audits, and time-bounded remediation for technical vulnerabilities. | Dependency scanning procedures and CVSS SLA targets documented in `docs/VULNERABILITY_AND_PATCH_MANAGEMENT.md`. | **Completed** |
| 18 | **A.8.12** | Data Leakage Prevention (DLP) & Token Safeguards | Technological | Preventing sensitive auth token leakage through URLs, browser history, or third-party web contexts. | Bearer token header transmission exclusively, CORS strict origin binding, and token blacklisting upon logout. | **Completed** |
| 19 | **A.8.20 / A.8.22** | Network Segregation & Perimeter Hardening | Technological | Network layer isolation of backend microservices, database tiers, and ingress rate limiting. | Reverse proxy / API Gateway configuration, database VPC subnet isolation, and DDoS protection standards. | **Completed** |
| 20 | **A.8.31** | Separation of Dev, Test & Production Environments | SSDLC | Strict separation of development environments, test fixtures, and hardened production infrastructure. | Spring profile isolation (`application-dev.properties` vs `application-prod.properties`) and distinct credentials. | **Completed** |

---

## 2. SOC 2 Trust Services Criteria (TSC) Standards

| # | Criteria ID | Trust Criteria Name | TSC Category | Criteria Description & Purpose | Media Jungle Implementation Reference | Status |
|---|---|---|---|---|---|---|
| 1 | **CC6.1** | Logical Access Controls & Authentication | Security | Restricts logical access to systems, applications, and protected media data to authorized users and processes. | Stateless Bearer token verification via `JwtAuthenticationFilter.java` and role authorization on API endpoints. | **Completed** |
| 2 | **CC6.2** | User Registration & Identity Verification | Access Control | New internal and external user accounts are registered, verified, and authorized before credentials are granted. | Dual-step registration flow with email OTP verification (`VerificationController.java`, `EmailService.java`) and `UserRegisterController.java`. | **Completed** |
| 3 | **CC6.3** | Access Revocation & Account Protection | Access Control | Logical access rights are modified, locked, or revoked when security thresholds or violations are triggered. | Automated account lockout after repeated failed login attempts, tracked via `failedLoginAttempts` and `lockTime` in `UserRegister.java`. | **Completed** |
| 4 | **CC6.6** | Perimeter Defense, Network Boundaries & CORS | Security / Access | Logical boundary protection to control ingress/egress network communications and prevent unauthorized cross-origin requests. | Granular Cross-Origin Resource Sharing (CORS) rules in `SecurityConfig.java` & `GlobalCorsConfig.java` restricting untrusted origins. | **Completed** |
| 5 | **CC6.7** | Data Transmission Security & Cryptographic Transit | Data Protection | Data transmitted across public and internal networks is encrypted using approved protocols to prevent eavesdropping. | HTTP Strict Transport Security (HSTS) with `max-age=31536000; includeSubDomains; preload` and TLS enforcement in `SecurityConfig.java`. | **Completed** |
| 6 | **CC6.8** | Malicious Code Prevention & Ingestion Checks | Data Protection | Measures prevent or detect the upload or execution of unauthorized or malicious files into the storage environment. | Multipart upload validation in `FileValidationService.java`: maximum size enforcement (10MB image, 50MB audio, 500MB video) & MIME checking. | **Completed** |
| 7 | **CC7.1** | Vulnerability Monitoring & Threat Detection | Incident Response | Systems are continuously monitored to identify vulnerabilities, anomalous behaviors, and security threats. | Aspect-Oriented Logging (`LoggingAspect.java`) intercepting controller execution, logging unauthorized access, and method trace monitoring. | **Completed** |
| 8 | **CC7.2** | Security Event Monitoring & Audit Trail | Monitoring & Logging | Audit logs are maintained for security events, authentication attempts, user registrations, and administrative tasks. | `AuditLogController.java` & `AuditLogService.java`: tracking username, client IP address, HTTP method, URL, status, and event description. | **Completed** |
| 9 | **CC7.3** | Incident Response & Access Denial Handling | Incident Response | Security incidents, access violations, and anomalous requests are captured, mitigated, and responded to appropriately. | `CustomAccessDeniedHandler.java` intercepting HTTP 403 access violations, returning structured JSON error payloads and auditing rejections. | **Completed** |
| 10 | **CC8.1** | Change Management, Build & System Operations | System Operations | Software changes, database schema evolutions, and system updates are authorized, tracked, tested, and systematically built. | Maven build lifecycle management (`pom.xml`), JPA DDL evolution controls (`spring.jpa.hibernate.ddl-auto=update`), and Git tracking. | **Completed** |
| 11 | **CC1.1 - CC1.4** | Control Environment & Separation of Duties | Governance | Tone at the top, ethical behavior, and administrative segregation between content managers, platform admins, and users. | Multi-tier role model in `UserRole.java`, restricted administrative creation APIs, and security governance policies. | **Completed** |
| 12 | **CC2.1 - CC2.2** | Security Communication & Incident Notification | Communication | Defined internal and external protocols for communicating security commitments, vulnerabilities, and incident alerts. | Documented reporting mechanisms in `docs/INCIDENT_RESPONSE.md` and responsible disclosure guidelines. | **Completed** |
| 13 | **CC3.1 - CC3.2** | Risk Assessment & Media Asset Classification | Risk Mitigation | Systematic identification, assessment, and classification of operational and media asset security risks. | Asset classification guidelines (Public vs. Premium Media) and threat models in `docs/SECURITY_CONFIGURATION.md`. | **Completed** |
| 14 | **CC4.1 - CC4.2** | Continuous Monitoring of Controls & Compliance | Monitoring | Ongoing automated and manual evaluations to ascertain whether security controls are operating effectively. | Automated test suite mappings (`TC01` - `TC25`) and operational health/metrics endpoints. | **Completed** |
| 15 | **CC5.1 - CC5.2** | Principle of Least Privilege & Database Segregation | Control Activities | Enforcing minimum necessary privileges for application runtime, database credentials, and operator access. | Database service account permissions restricted strictly to DML operations without administrative superuser access. | **Completed** |
| 16 | **CC6.4 / CC6.5** | Asset Discontinuation & Media Sanitization | Asset Management | Proper decommissioning and sanitization of outdated media assets, temporary DASH files, and revoked credentials. | License expiration enforcement via `LicenseController.java` and automated cleanup of expired assets. | **Completed** |
| 17 | **CC9.1 / CC9.2** | Vendor & Supply Chain Risk Management | Third-Party Risk | Managing risks associated with third-party vendors, OAuth providers, payment aggregators, and SMTP services. | Standardized evaluation framework in `docs/THIRD_PARTY_VENDOR_RISK_POLICY.md` and zero credential hardcoding. | **Completed** |
| 18 | **A1.1 - A1.3** | High Availability, Fault Tolerance & Recovery | Availability | Ensuring streaming service resilience, DASH multi-bitrate availability, and business continuity. | Thread pool management in `AsyncConfig.java` (`dash.executor`), database replication, and `docs/BACKUP_AND_RECOVERY.md`. | **Completed** |
| 19 | **C1.1 - C1.2** | Confidentiality & Media Digital Rights Management | Confidentiality | Protecting proprietary video content and premium streaming media from unauthorized public access or interception. | Segmented media storage, signed playback session tokens, and encrypted communication channels. | **Completed** |
| 20 | **PI1.1 - PI1.5** | Processing Integrity & Transaction Verifiability | Processing Integrity | Ensuring transactions (user registrations, subscription activations, plan renewals) are complete, accurate, and valid. | ACID database transaction management (`@Transactional`), DTO input validation, and payment state verification. | **Completed** |

---

## 3. Cross-Framework Compliance Mapping

| Security & Compliance Domain | ISO/IEC 27001:2022 Control | SOC 2 Trust Services Criteria | Reference Documentation |
| :--- | :--- | :--- | :--- |
| **Information Security Policy** | A.5.1 | CC1.1, CC1.2 | `SECURITY_STANDARDS.md`, `SecurityConfig.java` |
| **Access Control & RBAC** | A.9.1 / A.5.15 | CC6.1, CC6.3 | `docs/SOC2_SECURITY_CONTROLS.md` (Control 5) |
| **User Identity & Lifecycle** | A.9.2 / A.5.18 | CC6.2 | `docs/SOC2_SECURITY_CONTROLS.md` (Control 6) |
| **Authentication & Lockout** | A.9.4 / A.5.17 | CC6.1, CC6.3 | `docs/SOC2_SECURITY_CONTROLS.md` (Control 3) |
| **Cryptographic Protection** | A.10.1 / A.8.24 | CC6.7 | `docs/SOC2_SECURITY_CONTROLS.md` (Control 1) |
| **Token & Session Security** | A.10.2 / A.8.24 | CC6.1, CC6.7 | `docs/SOC2_SECURITY_CONTROLS.md` (Control 8) |
| **System Hardening & Headers**| A.12.1 / A.8.9 | CC6.6 | `docs/SECURITY_CONFIGURATION.md` |
| **Audit Logging & Review** | A.12.4 / A.8.15 | CC7.2 | `docs/SOC2_SECURITY_CONTROLS.md` (Control 9, 20) |
| **Malware & Upload Defense** | A.12.6 / A.8.7 | CC6.8 | `docs/SOC2_SECURITY_CONTROLS.md` (Control 16) |
| **Error Masking & SSDLC** | A.14.2 / A.8.28 | CC8.1 | `docs/SOC2_SECURITY_CONTROLS.md` (Control 15, 17) |
| **Threat Intelligence** | A.5.7 | CC7.1 | `docs/INCIDENT_RESPONSE.md` |
| **Data Deletion & Privacy** | A.5.10 / A.5.34 | CC6.5, Privacy P1-P8 | `docs/DATA_PROTECTION_AND_PRIVACY_POLICY.md` |
| **Vendor & Third-Party Risk**| A.5.19 / A.5.20 | CC9.1, CC9.2 | `docs/THIRD_PARTY_VENDOR_RISK_POLICY.md` |
| **Cloud Storage Security** | A.5.23 | CC6.1, C1.1 | `docs/SECURITY_CONFIGURATION.md` |
| **Backup & Availability** | A.5.29 / A.8.13 | A1.1, A1.2 | `docs/BACKUP_AND_RECOVERY.md` |
| **Vulnerability Patching** | A.8.8 | CC7.1 | `docs/VULNERABILITY_AND_PATCH_MANAGEMENT.md` |
| **Data Leakage Prevention** | A.8.12 | CC6.6, CC6.7 | `docs/SECURITY_CONFIGURATION.md` |
| **Network Segregation** | A.8.20 / A.8.22 | CC6.6 | `docs/SECURITY_CONFIGURATION.md` |
| **Environment Separation** | A.8.31 | CC8.1 | `docs/SECURITY_CONFIGURATION.md` |
| **Processing Integrity** | A.14.2 | PI1.1 - PI1.5 | `docs/SOC2_SECURITY_CONTROLS.md` (Control 15) |

---

## 4. Security Standards REST API Endpoints

The Media Jungle backend exposes native REST API endpoints to inspect, verify, and monitor the compliance status of these standards:

- **`GET /api/v2/security-standards`**: Complete catalogue of implemented standards.
- **`GET /api/v2/security-standards/iso27001`**: Returns ISO 27001 standards.
- **`GET /api/v2/security-standards/soc2`**: Returns SOC 2 Trust Services Criteria standards.
- **`GET /api/v2/security-standards/summary`**: High-level compliance statistics (total standards, breakdown by framework, coverage percentage).
