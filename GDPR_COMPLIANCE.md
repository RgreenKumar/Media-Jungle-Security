# GDPR Compliance Implementation — Media Jungle

This document lists the 25 concrete implementation tasks added to this codebase to bring the
`mediaReact` (React) and `mediaSpring` (Spring Boot) applications closer to GDPR compliance.
Every task below is marked in the source code with a `GDPR-TASK-NN` comment so it can be found
with a simple search (`grep -rn "GDPR-TASK-" .`).

> Scope note: the `ott_project` (Flutter apps for mobile/TV/web) was left unmodified in this pass.
> The concepts below (consent capture, export, erasure) map onto REST calls to the same
> `/api/gdpr/*` endpoints, so the Flutter clients can be wired up to them next.

## Backend — `mediaSpring`

| # | Task | File(s) |
|---|------|---------|
| 01 | Explicit `consentGiven` field captured at registration | `userregister/UserRegister.java` |
| 02 | `consentTimestamp` — proof of when consent was given | `userregister/UserRegister.java` |
| 03 | Granular, unbundled `marketingOptIn` consent | `userregister/UserRegister.java` |
| 04 | `termsVersion` — track which policy version was accepted | `userregister/UserRegister.java` |
| 05 | Registration rejected server-side without consent | `userregister/UserRegisterController.java` |
| 06 | `GdprAuditLog` entity — accountability audit trail | `gdpr/GdprAuditLog.java` |
| 07 | Audit log repository | `gdpr/GdprAuditLogRepository.java` |
| 08 | Right to Access / Data Portability (export) | `gdpr/GdprService.java` |
| 09 | Right to Erasure via anonymization | `gdpr/GdprService.java` |
| 10 | Right to withdraw / update consent | `gdpr/GdprService.java` |
| 11 | Every GDPR action written to the audit trail | `gdpr/GdprService.java` |
| 12 | `GET /api/gdpr/export/{userId}` endpoint | `gdpr/GdprController.java` |
| 13 | `DELETE /api/gdpr/erase/{userId}` endpoint | `gdpr/GdprController.java` |
| 14 | `PUT /api/gdpr/consent/{userId}` endpoint | `gdpr/GdprController.java` |
| 15 | `GET /api/gdpr/consent/{userId}` (status + audit trail) | `gdpr/GdprController.java` |
| 16 | Repository query for stale/unconfirmed accounts | `userregister/UserRegisterRepository.java` |
| 17 | Scheduled data-retention job (storage limitation) | `gdpr/DataRetentionScheduler.java`, `MediaSpringApplication.java` |
| 18 | Security baseline: stateless sessions + CORS wiring | `SecurityConfig.java` |
| 19 | Explicit CORS allow-list (no wildcard origins) | `googleLogin/GlobalCorsConfig.java` |
| 20 | GDPR configuration block (retention, DPO contact, secure cookies) | `resources/application.properties` |
| 21 | PII/password/JWT/email redaction in application logs | `LoggingAspect.java` |

## Frontend — `mediaReact`

| # | Task | File(s) |
|---|------|---------|
| 22 | Cookie consent banner (essential vs. optional cookies) | `src/CookieConsent.js` |
| 23 | Banner mounted globally across the app | `src/App.js` |
| 24 | Mandatory + granular consent checkboxes at registration | `src/user/Screens/Register.js` |
| 25 | Real Privacy Policy content + self-service "Download my data" / "Delete my account" | `src/user/Screens/PrivacyPolicy.js`, `src/user/ViewProfile.js` |

## What is intentionally out of scope for this pass

- Field-level encryption at rest for the database (recommend a Hibernate `AttributeConverter`
  with a KMS-managed key for `email`/`mobnum` in a follow-up).
- Rate limiting on the authentication endpoints.
- A signed Data Processing Agreement (DPA) template for third-party processors (payment
  gateway, email provider) — this is a legal/contractual task, not a code task.
- Wiring the Flutter `ott_project` clients to the new `/api/gdpr/*` endpoints.

## How to verify

```bash
# Find every GDPR task marker in the codebase
grep -rn "GDPR-TASK-" mediaReact/src mediaSpring/src
```
