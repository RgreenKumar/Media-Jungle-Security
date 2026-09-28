# Media Jungle - ISO/IEC 27001:2022 Security Controls & Compliance Matrix
**Application**: Media Jungle OTT Platform  
**Compliance Target**: ISO/IEC 27001:2022 Information Security Management System (ISMS)  
**Status**: 20 Standards Defined, Implemented & Verified  
**Date**: September 2026

---

## Executive Summary
This document provides complete technical documentation, control mappings, implementation references, and audit verification procedures for the **20 ISO/IEC 27001:2022 controls** implemented across the Media Jungle platform. The controls span Organizational (A.5), People (A.6), Physical (A.7), and Technological (A.8) domains to ensure end-to-end information security, confidentiality, integrity, and availability.

---

## Control 1 — A.5.1: Policies for Information Security
- **Domain**: A.5 Organizational Controls
- **Control Objective**: Management direction and support for information security in accordance with business requirements and relevant laws and regulations.
- **Implementation in Media Jungle**:
  - Centralized security policies defined in `SecurityConfig.java` configuring authentication, authorization, CORS, and HTTP security headers.
  - Published and version-controlled security standards across `SECURITY_STANDARDS.md` and `docs/`.
  - Environment-driven operational policies via `application.properties` (password policies, lockout rules, file quotas).
- **Audit Verification**:
  - Confirm security policy files are tracked in Git, reviewed periodically, and enforce zero hardcoded secrets.

---

## Control 2 — A.9.1 / A.5.15: Access Control & Role Enforcement
- **Domain**: A.5 Organizational Controls / A.9 Access Control
- **Control Objective**: Ensure that users only access the information and functions they have been authorized to use.
- **Implementation in Media Jungle**:
  - Role-Based Access Control (RBAC) enforced with Spring Security `@EnableMethodSecurity(prePostEnabled = true)`.
  - Endpoint protection utilizing `@PreAuthorize("hasRole('ADMIN')")` on critical administrative functions.
  - Granular division between `ROLE_USER` (media consumers) and `ROLE_ADMIN` (platform operators).
- **Audit Verification**:
  - Attempting to access administrative routes (`/api/v2/security-standards`, `/api/v2/AddUser`) with a standard user JWT returns `HTTP 403 Forbidden`.

---

## Control 3 — A.9.2 / A.5.18: User Registration & Access Rights Management
- **Domain**: A.5 Organizational Controls / A.9 Access Control
- **Control Objective**: Control the allocation and modification of user access rights throughout their full lifecycle.
- **Implementation in Media Jungle**:
  - User registration handled via `UserRegisterController.java` with email identity verification.
  - Strict role assignment defaults to `ROLE_USER`; administrative privileges can only be provisioned by super-administrators.
  - Session and token revocation via `TokenBlacklist.java` upon account suspension or logout.
- **Audit Verification**:
  - Verify new registrations cannot inject elevated roles via request payloads.

---

## Control 4 — A.9.4 / A.5.17: Secret Authentication & Account Lockout
- **Domain**: A.5 Organizational Controls / A.9 Access Control
- **Control Objective**: Protect authentication mechanisms against automated credential attacks, brute-force, and credential stuffing.
- **Implementation in Media Jungle**:
  - Configurable brute-force defense: `security.max-login-attempts=5` consecutive failed attempts trigger a 15-minute account lock (`security.lock-duration-minutes=15`).
  - Account state tracked in `UserRegister.java` (`failedLoginAttempts`, `lockTime`).
  - Passwords must satisfy complexity requirements (minimum 8 characters, uppercase, lowercase, digit, special character).
- **Audit Verification**:
  - Simulate 5 consecutive failed logins; the 6th attempt must be rejected with an account lockout message.

---

## Control 5 — A.10.1 / A.8.24: Cryptographic Controls & Password Hashing
- **Domain**: A.8 Technological Controls / A.10 Cryptography
- **Control Objective**: Protect the confidentiality, authenticity, and integrity of stored credentials and sensitive data.
- **Implementation in Media Jungle**:
  - One-way cryptographic password hashing utilizing BCrypt (`BCryptPasswordEncoder` with strength factor 10).
  - Passwords marked `@JsonProperty(access = Access.WRITE_ONLY)` and explicitly cleared from memory prior to API serialization.
  - Zero plaintext passwords in database tables or log files.
- **Audit Verification**:
  - Inspect database records to verify all hashes start with `$2a$10$` and are 60 characters long.

---

## Control 6 — A.10.2 / A.8.24: Key Management & Token Security
- **Domain**: A.8 Technological Controls / A.10 Cryptography
- **Control Objective**: Manage cryptographic keys throughout their lifecycle from generation, secure storage, to revocation.
- **Implementation in Media Jungle**:
  - Stateless JSON Web Tokens (JWT) signed using HMAC-SHA256 with a 256-bit cryptographically secure secret.
  - Key loaded securely from environment variable (`${JWT_SECRET}`).
  - Short-lived token validity enforced with automatic expiration checks in `JwtAuthenticationFilter.java`.
- **Audit Verification**:
  - Modifying the JWT signature or payload causes immediate token rejection with `HTTP 401 Unauthorized`.

---

## Control 7 — A.12.1 / A.8.9: Secure Configuration & HTTP Security Headers
- **Domain**: A.8 Technological Controls / A.12 Operations Security
- **Control Objective**: Ensure that configurations of hardware and software are documented, reviewed, and secured against tampering.
- **Implementation in Media Jungle**:
  - Hardened HTTP security headers configured in `SecurityConfig.java`:
    - `X-Frame-Options: DENY` (Anti-Clickjacking)
    - `X-Content-Type-Options: nosniff` (Anti-MIME Sniffing)
    - `Content-Security-Policy: default-src 'self'` (XSS defense)
    - `Strict-Transport-Security: max-age=31536000; includeSubDomains; preload` (HSTS)
    - `Permissions-Policy: camera=(), microphone=(), geolocation=()` (Device API restriction)
- **Audit Verification**:
  - Inspect curl / browser HTTP response headers to confirm presence of all security headers.

---

## Control 8 — A.12.4 / A.8.15: Logging & Event Monitoring
- **Domain**: A.8 Technological Controls / A.12 Operations Security
- **Control Objective**: Record events, generate evidence, and protect log files against unauthorized tampering or deletion.
- **Implementation in Media Jungle**:
  - Aspect-Oriented Logging via `LoggingAspect.java` intercepting all controller executions.
  - Persistent security audit logs recorded in `AuditLog` entity, tracking username, client IP, endpoint, HTTP method, status, and timestamp.
  - Rolling log files with gzip compression (`mediajungle.log.%d.gz`).
- **Audit Verification**:
  - Verify audit table records authentication events, user updates, and access denials.

---

## Control 9 — A.12.6 / A.8.7: Malware Protection & Secure File Handling
- **Domain**: A.8 Technological Controls / A.12 Operations Security
- **Control Objective**: Protect information processing facilities and software from malware and unauthorized file execution.
- **Implementation in Media Jungle**:
  - Multipart upload validation executed by `FileValidationService.java`.
  - Extension whitelist and real MIME type inspection against file content to prevent malicious disguise.
  - Size quota limits: 10MB images, 50MB audio tracks, 500MB video files.
  - Uploaded files stored outside the web root execution context with sanitized filenames.
- **Audit Verification**:
  - Attempting to upload `.exe`, `.sh`, or disguised MIME payloads is rejected with `HTTP 400 Bad Request`.

---

## Control 10 — A.14.2 / A.8.28: Secure Software Development & Error Masking
- **Domain**: A.8 Technological Controls / A.14 System Acquisition, Development & Maintenance
- **Control Objective**: Apply secure coding principles to develop resilient software and prevent sensitive information leakage.
- **Implementation in Media Jungle**:
  - Centralized `GlobalExceptionHandler.java` catches uncaught exceptions and masks internal implementation details.
  - Generic client responses (`Internal server error`, `Invalid credentials`) prevent database schema or stack trace leakage.
  - Strong input validation using DTOs, regex constraints, and Bean Validation annotations.
- **Audit Verification**:
  - Triggering deliberate exceptions (e.g. malformed JSON or SQL inputs) returns clean error payloads without JVM stack traces.

---

## Control 11 — A.5.7: Threat Intelligence & Attack Vector Profiling
- **Domain**: A.5 Organizational Controls
- **Control Objective**: Information relating to information security threats shall be collected and analyzed to produce threat intelligence.
- **Implementation in Media Jungle**:
  - Threat profiling for media streaming vulnerabilities: credential stuffing, stream piracy, token replay, and upload injection.
  - Active ingestion of National Vulnerability Database (NVD) alerts for Spring Boot, Tomcat, and FFmpeg components.
  - IP reputation and anomaly monitoring captured via access logging aspects.
- **Audit Verification**:
  - Annual threat modeling review documented in `docs/INCIDENT_RESPONSE.md`.

---

## Control 12 — A.5.10 / A.8.10: Information Deletion & Data Sanitization
- **Domain**: A.5 Organizational Controls / A.8 Technological Controls
- **Control Objective**: Information stored in information systems, devices, or in any other storage media shall be deleted when no longer required.
- **Implementation in Media Jungle**:
  - Soft and hard user deletion policies complying with GDPR/CCPA Right to Erasure.
  - Orphaned media cleanup: unlinked video chunks, temporary DASH segments, and expired licenses purged automatically.
  - Database row removal cascades cleanly across playlists, subscriptions, and profile images.
- **Audit Verification**:
  - Test user account deletion endpoint and verify personal data is sanitized from primary user tables.

---

## Control 13 — A.5.19 / A.5.20: Supplier Relationships & Third-Party Security
- **Domain**: A.5 Organizational Controls
- **Control Objective**: Processes and procedures shall be defined and implemented to manage the information security risks associated with the use of supplier's products or services.
- **Implementation in Media Jungle**:
  - Third-party API governance: Google OAuth 2.0 (`AuthController.java`), SMTP email relays (`EmailService.java`), and payment gateways.
  - API credentials stored exclusively in environment variables; zero hardcoding.
  - Strict HTTPS TLS 1.3 requirements for all outbound webhook and API communications.
- **Audit Verification**:
  - Verify supplier security agreements and credentials management in `docs/THIRD_PARTY_VENDOR_RISK_POLICY.md`.

---

## Control 14 — A.5.23: Information Security for Cloud Media & Asset Storage
- **Domain**: A.5 Organizational Controls
- **Control Objective**: Processes for acquisition, use, management, and exit from cloud services shall be established in accordance with the organization's information security requirements.
- **Implementation in Media Jungle**:
  - Physical asset separation: Public assets (posters, banners) vs. Private assets (premium video streams, DRM licenses) stored in isolated paths (`app.storage.public-dir` vs. `app.storage.private-dir`).
  - Pre-signed URL or authenticated streaming tokens required for accessing private media segments.
- **Audit Verification**:
  - Attempting direct unauthenticated HTTP access to `app.storage.private-dir` files is denied.

---

## Control 15 — A.5.29 / A.8.13: Business Continuity & Redundancy for Media Streaming
- **Domain**: A.5 Organizational Controls / A.8 Technological Controls
- **Control Objective**: Organizations shall plan, implement, maintain, and test information security continuity and data backup procedures.
- **Implementation in Media Jungle**:
  - Automated database backup procedures (PostgreSQL pg_dump scripts documented in `BACKUP_AND_RECOVERY.md`).
  - Target Recovery Time Objective (RTO) $\le 4$ hours, Recovery Point Objective (RPO) $\le 1$ hour.
  - Storage redundancy across primary storage and encrypted secondary backup archives.
- **Audit Verification**:
  - Biannual disaster recovery restoration drills logged and validated against test environments.

---

## Control 16 — A.5.34 / A.8.11: Privacy, PII Protection & Data Masking
- **Domain**: A.5 Organizational Controls / A.8 Technological Controls
- **Control Objective**: The organization shall identify and meet the requirements regarding the preservation of privacy and protection of Personally Identifiable Information (PII).
- **Implementation in Media Jungle**:
  - Sensitive field masking in application log outputs (passwords, OTP tokens, JWT secrets replaced with `***MASKED***`).
  - Minimal collection of user PII: only email and username required for streaming service activation.
  - Data protection guidelines documented in `docs/DATA_PROTECTION_AND_PRIVACY_POLICY.md`.
- **Audit Verification**:
  - Review log files (`mediajungle.log`) to confirm zero plaintext passwords or auth tokens appear in logs.

---

## Control 17 — A.8.8: Technical Vulnerability Management & Dependency Patching
- **Domain**: A.8 Technological Controls
- **Control Objective**: Information about technical vulnerabilities of information systems being used shall be obtained, evaluated, and addressed.
- **Implementation in Media Jungle**:
  - Software Composition Analysis (SCA) implemented through Maven dependency audit tools and OWASP Dependency-Check.
  - Critical/High severity CVE remediation timeline: Critical $\le 7$ days, High $\le 30$ days.
  - Documented patch procedures in `docs/VULNERABILITY_AND_PATCH_MANAGEMENT.md`.
- **Audit Verification**:
  - Execute `mvn dependency-check:check` or GitHub Dependabot alerts to verify zero unpatched critical vulnerabilities.

---

## Control 18 — A.8.12: Data Leakage Prevention (DLP) & Token Safeguards
- **Domain**: A.8 Technological Controls
- **Control Objective**: Data leakage prevention measures shall be applied to systems, networks, and any other devices that process, store, or transmit sensitive information.
- **Implementation in Media Jungle**:
  - Bearer tokens transmitted exclusively via HTTP `Authorization: Bearer <token>` headers; prohibited from query strings to prevent URL access-log leakage.
  - Cross-Origin Resource Sharing (CORS) whitelist restricting external domains from reading authenticated response payloads.
- **Audit Verification**:
  - Verify server access logs do not contain sensitive authorization tokens in URL paths.

---

## Control 19 — A.8.20 / A.8.22: Network Segregation & Web Application Firewall (WAF)
- **Domain**: A.8 Technological Controls
- **Control Objective**: Networks shall be secured, managed, and controlled to protect information in systems and applications.
- **Implementation in Media Jungle**:
  - Network perimeter defense: Database (PostgreSQL port 5432) isolated in private VPC subnets with zero public ingress.
  - Reverse proxy / API Gateway handles TLS termination, DDoS rate limiting, and request filtering before forwarding to Spring Boot.
- **Audit Verification**:
  - Port scans verify database and backend internal ports are unreachable from the public internet.

---

## Control 20 — A.8.31: Separation of Development, Test & Production Environments
- **Domain**: A.8 Technological Controls
- **Control Objective**: Development, testing, and production environments shall be separated and secured.
- **Implementation in Media Jungle**:
  - Strict profile isolation (`application-dev.properties` vs. `application-prod.properties`).
  - Production databases use dedicated credentials and encrypted connections.
  - Test/dummy media data prohibited from contaminating production streaming repositories.
- **Audit Verification**:
  - Inspect production deployment pipeline to confirm development debugging flags (`spring.jpa.show-sql=false`) are disabled.
