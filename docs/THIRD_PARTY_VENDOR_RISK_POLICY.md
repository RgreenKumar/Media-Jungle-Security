# Media Jungle - Third-Party & Vendor Risk Management Policy
**Compliance Standards**: ISO/IEC 27001:2022 (Controls A.5.19, A.5.20, A.5.21, A.5.22, A.5.23) & SOC 2 Trust Services Criteria (CC9.1, CC9.2)  
**Classification**: Enterprise Governance Policy  
**Target Platform**: Media Jungle OTT Streaming Platform  
**Effective Date**: September 2026

---

## 1. Overview & Objective
Modern OTT platforms rely on cloud providers, identity brokers, content delivery networks (CDNs), and payment processors. This policy mandates the processes for vetting, onboarding, monitoring, and retiring third-party vendors and external software services that interact with Media Jungle data or infrastructure.

---

## 2. Vendor Inventory & Classification

| Vendor Category | Integrated Services | Risk Tier | Primary Security Requirements |
| :--- | :--- | :--- | :--- |
| **Cloud & Infrastructure** | Cloud storage (AWS S3 / Azure Blob), compute instances | **Tier 1 (High)** | SOC 2 Type II / ISO 27001 certification, encryption at rest, IAM least privilege |
| **Authentication & Identity** | Google OAuth 2.0 (`AuthController.java`) | **Tier 1 (High)** | OpenID Connect standard compliance, TLS 1.3, token signature validation |
| **Payment Gateways** | Stripe / Razorpay API integrations | **Tier 1 (High)** | PCI-DSS Level 1 compliance, client-side tokenization, webhook HMAC validation |
| **Transactional Email / SMTP**| Google SMTP / SendGrid (`EmailService.java`) | **Tier 2 (Medium)** | SPF, DKIM, DMARC alignment, TLS enforced transport, zero credential storage in code |
| **Media Transcoding Tooling** | FFmpeg binary dependencies | **Tier 2 (Medium)** | Static binary integrity verification, isolated execution environment, restricted system calls |

---

## 3. Vendor Assessment & Due Diligence
Prior to integrating any third-party library or service:
1. **Security Evidence Review**: Collect and review vendor SOC 2 Type II or ISO 27001:2022 audit reports (must be within 12 months).
2. **Credential Isolation**: Vendors are assigned isolated service credentials. Secrets must be passed through environment variables (`${MAIL_PASSWORD}`, `${GOOGLE_CLIENT_SECRET}`) and managed via vault services.
3. **Data Processing Agreements (DPA)**: Vendor must sign contractual clauses ensuring GDPR/CCPA data privacy compliance, prompt breach notification ($\le 48$ hours), and confidentiality commitments.

---

## 4. Continuous Monitoring & Annual Review
- Review vendor security postures, incident histories, and compliance certificates annually.
- Monitor third-party API reliability, latency, and status dashboards.
- Fallback mechanisms: In the event of an external provider outage (e.g. SMTP downtime), Media Jungle gracefully degrades or logs errors without exposing stack traces or crashing core media streaming services.
