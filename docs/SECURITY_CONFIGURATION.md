# Media Jungle - Security Configuration Management Guide
**SOC 2 Compliance Reference**: Control 19 — Security Configuration Management  
**Configuration File**: `mediaSpring/src/main/resources/application.properties` & Environment Variables

---

## 1. Overview
All security parameters across the Media Jungle platform are centralized, environment-driven, and configurable without modifying application code. This ensures zero hardcoded secrets and enables strict separation between local development and hardened production configurations.

---

## 2. Centralized Security Properties Reference

| Property Key | Default Value | Environment Variable | SOC 2 Control | Description |
| :--- | :--- | :--- | :--- | :--- |
| `security.password.min-length` | `8` | `PASSWORD_MIN_LENGTH` | Control 2 | Minimum character length for user/admin passwords |
| `security.password.require-uppercase` | `true` | `PASSWORD_REQ_UPPER` | Control 2 | Requires $\ge 1$ uppercase alphabetical character (A-Z) |
| `security.password.require-lowercase` | `true` | `PASSWORD_REQ_LOWER` | Control 2 | Requires $\ge 1$ lowercase alphabetical character (a-z) |
| `security.password.require-digit` | `true` | `PASSWORD_REQ_DIGIT` | Control 2 | Requires $\ge 1$ numerical digit (0-9) |
| `security.password.require-special` | `true` | `PASSWORD_REQ_SPECIAL` | Control 2 | Requires $\ge 1$ special character (`!@#$%^&*()_+-=[]{}...`) |
| `security.max-login-attempts` | `5` | `LOGIN_MAX_ATTEMPTS` | Control 3, 10 | Consecutive failed attempts before locking account |
| `security.lock-duration-minutes` | `15` | `LOCKOUT_MINUTES` | Control 3 | Duration in minutes before auto-unlocking account |
| `jwt.expiration-ms` | `86400000` (24h) | `JWT_EXPIRATION_MS` | Control 8 | Lifetime of issued authentication JWT in milliseconds |
| `jwt.secret` | `${JWT_SECRET}` | `JWT_SECRET` | Control 8, 13 | HMAC-SHA256 private key for signing tokens ($\ge 256$ bits) |
| `security.cors.allowed-origins` | `http://localhost:3000...` | `CORS_ALLOWED_ORIGINS`| Control 11 | Comma-delimited list of trusted client origins |
| `security.upload.max-image-size` | `10485760` (10MB) | `MAX_IMAGE_SIZE` | Control 16 | Maximum file size for image avatars and posters |
| `security.upload.max-audio-size` | `52428800` (50MB) | `MAX_AUDIO_SIZE` | Control 16 | Maximum file size for uploaded audio tracks |
| `security.upload.max-video-size` | `524288000` (500MB)| `MAX_VIDEO_SIZE` | Control 16 | Maximum file size for raw video uploads |

---

## 3. Production vs Development Configuration

### 3.1 Local Development Defaults
- CORS permits `http://localhost:3000`, `http://localhost:4203`, `http://127.0.0.1:3000`.
- Standard HTTP communication permitted for development servers.
- Automatic mock OTP fallback when SMTP is unconfigured to facilitate seamless local testing.

### 3.2 Production Hardening Requirements
- **TLS/HTTPS Termination**: In production, traffic must terminate via Nginx, Cloudflare, or AWS ALB with TLS 1.3.
- **HSTS Enforcement**: Enforced with `maxAgeInSeconds(31536000)`, `includeSubDomains(true)`, `preload(true)`.
- **CORS Lock-Down**: Restrict `security.cors.allowed-origins` strictly to production domain (e.g., `https://mediajungle.com`).
- **Secrets Injection**: Pass `DB_PASSWORD`, `JWT_SECRET`, `MAIL_PASSWORD` via system environment variables, AWS Secrets Manager, or HashiCorp Vault. Never commit to `.env` or Git.

---

## 4. HTTP Security Headers
Managed in `SecurityConfig.java`:
- **Content-Security-Policy (CSP)**: Restricts scripts, styles, images, and media sources to prevent Cross-Site Scripting (XSS).
- **X-Frame-Options**: `DENY` prevents Clickjacking.
- **X-Content-Type-Options**: `nosniff` disables MIME sniffing attacks.
- **Referrer-Policy**: `strict-origin-when-cross-origin`.
- **Permissions-Policy**: Restricts unneeded device APIs (`camera=(), microphone=(), geolocation=(), payment=(), usb=()`).
