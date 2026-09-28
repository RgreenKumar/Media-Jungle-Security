# Media Jungle - Data Protection & Privacy Policy
**Compliance Standards**: ISO/IEC 27001:2022 (A.5.34, A.8.10, A.8.11) & SOC 2 Trust Services Criteria (Privacy P1.1 - P8.1)  
**Classification**: Public / Internal Governance  
**Target Platform**: Media Jungle OTT Streaming Platform  
**Effective Date**: September 2026

---

## 1. Purpose & Scope
This policy establishes mandatory data governance, personal data handling, privacy preservation, and data subject rights enforcement across all components of the Media Jungle platform (consumer streaming portal, administrative operations, and storage subsystems).

---

## 2. Personal Data Minimization (Principle P1 / ISO A.5.34)
Media Jungle strictly adheres to the principle of data minimization:
- **Collected Data Points**:
  - Primary identity: Email address and username.
  - Authentication: One-way BCrypt password hash and salt.
  - Streaming activity: Watch history timestamps and user-curated playlist items.
- **Prohibited Data**:
  - No storage of raw financial instruments (credit card numbers, CVVs). Payment processing is delegated entirely to PCI-DSS certified payment processors.
  - No collection of sensitive PII (government ID numbers, biometric identity data).

---

## 3. Data Protection Safeguards (ISO A.8.11, A.8.24 / SOC 2 CC6.7)
1. **Data in Transit**:
   - All network communication enforced with TLS 1.3/HTTPS using modern cipher suites.
   - HSTS header `max-age=31536000; includeSubDomains; preload` prevents protocol downgrade attacks.
2. **Data at Rest**:
   - Database volumes encrypted using AES-256 at the block/storage layer.
   - Authentication secrets hashed via BCrypt with cost factor 10.
   - JSON serialization guards: Sensitive attributes (passwords, tokens) marked `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` to guarantee zero leakage in REST API payloads.
3. **Log Sanitization**:
   - Centralized logging intercepts (`LoggingAspect.java`) scrub credentials, JWT authorization tokens, and OTP codes before writing to log sinks.

---

## 4. Data Subject Rights & Right to Erasure (GDPR Art. 17 / CCPA / ISO A.8.10)
Users retain full control over their personal data:
- **Right of Access & Portability**: Users can inspect their account profile, viewing preferences, and subscription tier via `/api/v2/viewprofile`.
- **Right to Rectification**: Profile modifications supported via authenticated endpoints with ownership validation.
- **Right to Erasure ("Right to be Forgotten")**:
  - Account deletion requests trigger complete removal of identity records from `user_register` and `add_user` tables.
  - Cascading deletion permanently unlinks associated playlists, custom libraries, and activity tokens.
  - Residual backup copies overwritten within standard 30-day backup retention cycles.

---

## 5. Data Retention & Media Discontinuation (ISO A.5.10 / SOC 2 CC6.5)
- **Active Streaming Logs**: Stored for 90 days for operational analysis, then automatically archived and compressed.
- **Audit Trails**: Security logs (`AuditLog`) retained for 365 days in compliance with SOC 2 CC7.2 audit evidence requirements.
- **Discontinued Media Assets**: When licenses expire (`LicenseController`), raw video masters, transcoding temp files, and DASH manifests are wiped from storage directories.

---

## 6. Roles & Responsibilities
- **Data Protection Officer (DPO)**: Oversees privacy compliance, reviews data flow architecture, and handles regulatory inquiries.
- **Platform Engineers**: Enforce data classification, maintain masking filters, and execute privacy-by-design standards in development.
