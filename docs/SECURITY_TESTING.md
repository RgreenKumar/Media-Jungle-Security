# Media Jungle - Security Testing Guide & Test Case Matrix
**SOC 2 Compliance Reference**: Controls 1 through 20  
**Test Suite**: `src/test/java/com/VsmartEngine/MediaJungle/Soc2SecurityControlsTest.java`

---

## 1. Overview
This document specifies the verification methodology, test execution instructions, and test matrix covering 25 formal security test cases (TC01 through TC25) corresponding to the 20 SOC 2 security controls implemented in Media Jungle.

---

## 2. Test Case Matrix (TC01 — TC25)

| Test ID | Test Case Title | Target Control | Description / Scenario | Expected Outcome |
| :--- | :--- | :--- | :--- | :--- |
| **TC01** | Registration with valid password | Control 1, 2 | Register user with password meeting all complexity criteria | HTTP 200 OK, password hashed in DB |
| **TC02** | Registration with weak password | Control 2 | Register user with password lacking uppercase or special char | HTTP 400 Bad Request with policy error |
| **TC03** | Duplicate email registration | Control 6 | Attempt to register existing email address | HTTP 409 Conflict |
| **TC04** | Duplicate username registration | Control 6 | Attempt to register existing username | HTTP 409 Conflict |
| **TC05** | Password is hashed | Control 1, 14 | Verify database stores BCrypt hash (`$2a$10$...`), never plaintext | Hash verified with `isBCryptHash() == true` |
| **TC06** | Correct password login | Control 1, 3 | Authenticate with valid credentials | HTTP 200 OK, JWT returned, failed attempts = 0 |
| **TC07** | Incorrect password login | Control 3, 10 | Authenticate with invalid password | HTTP 401 Unauthorized, generic error message |
| **TC08** | Repeated failed login attempts | Control 3, 10 | Submit multiple invalid login attempts | Counter increments, logged in `audit_log` |
| **TC09** | Account lockout | Control 3 | Exceed 5 consecutive failed login attempts | HTTP 423 Locked, account locked for 15 minutes |
| **TC10** | Successful login resets attempts | Control 3 | Successful login after prior failures | `failed_login_attempts` reset to 0 |
| **TC11** | Unauthorized API access | Control 5, 8 | Access protected endpoint without JWT | HTTP 401 Unauthorized / 403 Forbidden |
| **TC12** | USER accessing ADMIN endpoint | Control 5, 7 | Call admin endpoint with valid USER token | HTTP 403 Forbidden |
| **TC13** | ADMIN accessing ADMIN endpoint | Control 5, 7 | Call admin endpoint with valid ADMIN token | HTTP 200 OK |
| **TC14** | Expired JWT/session | Control 8 | Provide expired token to authenticated API | HTTP 401 Unauthorized / Rejected |
| **TC15** | Invalid / Malformed JWT | Control 8 | Provide altered or corrupted JWT signature | HTTP 401 Unauthorized / Rejected |
| **TC16** | Password update | Control 1, 2 | Authenticated user changes password meeting policy | HTTP 200 OK, updated hash in DB |
| **TC17** | Password reset with OTP | Control 6, 7 | Reset password after verifying email OTP | HTTP 200 OK; bypass without OTP returns 400 |
| **TC18** | Password not returned in API | Control 1, 12 | Query user profile and list endpoints | Password field is null / excluded from JSON |
| **TC19** | Password not written to logs | Control 1, 9 | Review audit logs and server logs | Zero password values found in logs |
| **TC20** | Malicious file upload blocked | Control 16 | Attempt uploading executable (`.exe`, `.sh`, `.php`) | HTTP 400 Bad Request, upload rejected |
| **TC21** | Path traversal filename sanitized | Control 16 | Upload file with `../../traversal.png` | Sanitized to safe random/clean filename |
| **TC22** | Oversized file upload rejected | Control 16 | Upload image exceeding 10MB limit | HTTP 413 Payload Too Large |
| **TC23** | Invalid MIME type rejected | Control 16 | Upload audio file disguised as image | HTTP 400 Bad Request, MIME check fails |
| **TC24** | Security event generated | Control 9, 20 | Perform security action (lockout, login, upload) | Audit log record inserted in PostgreSQL |
| **TC25** | Secrets absent from source code | Control 13 | Scan Java/React sources for hardcoded passwords | Zero hardcoded passwords found |

---

## 3. Running Automated Tests
Run the comprehensive automated JUnit test suite using Maven and JDK 21:

```powershell
$env:JAVA_HOME = "C:\Users\RH\.jdks\ms-21.0.11"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
mvn test -Dtest=Soc2SecurityControlsTest
```
