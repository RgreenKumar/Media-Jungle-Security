package com.VsmartEngine.MediaJungle.gdpr;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.VsmartEngine.MediaJungle.userregister.UserRegister;

/**
 * GDPR-TASK-12..15: Self-service data-subject-rights API. Kept as its own controller (rather than
 * bolted onto UserRegisterController) so the GDPR surface area is easy to find, review, and audit.
 */
@CrossOrigin()
@RestController
@RequestMapping("/api/gdpr")
public class GdprController {

    private static final Logger logger = LoggerFactory.getLogger(GdprController.class);

    @Autowired
    private GdprService gdprService;

    // GDPR-TASK-12: Right to Access / Data Portability (Art. 15 & 20) - lets a logged-in user
    // download everything the platform holds about them as JSON.
    @GetMapping("/export/{userId}")
    public ResponseEntity<?> exportData(@PathVariable Long userId) {
        Optional<Map<String, Object>> data = gdprService.exportUserData(userId);
        if (data.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"User not found\"}");
        }
        return ResponseEntity.ok(data.get());
    }

    // GDPR-TASK-13: Right to Erasure / "right to be forgotten" (Art. 17). Anonymises the account
    // (see GdprService#eraseUserData for why anonymisation is used instead of a hard delete).
    @DeleteMapping("/erase/{userId}")
    public ResponseEntity<?> eraseData(@PathVariable Long userId) {
        boolean erased = gdprService.eraseUserData(userId);
        if (!erased) {
            logger.warn("GDPR erasure requested for unknown userId={}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"User not found\"}");
        }
        logger.info("GDPR erasure completed for userId={}", userId);
        return ResponseEntity.ok("{\"message\": \"Your account and personal data have been erased.\"}");
    }

    // GDPR-TASK-14: Right to withdraw / update consent at any time (Art. 7(3)).
    @PutMapping("/consent/{userId}")
    public ResponseEntity<?> updateConsent(@PathVariable Long userId, @RequestBody Map<String, Boolean> body) {
        boolean consentGiven = body.getOrDefault("consentGiven", false);
        boolean marketingOptIn = body.getOrDefault("marketingOptIn", false);
        Optional<UserRegister> updated = gdprService.updateConsent(userId, consentGiven, marketingOptIn);
        if (updated.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"User not found\"}");
        }
        return ResponseEntity.ok("{\"message\": \"Consent preferences updated.\"}");
    }

    // GDPR-TASK-15: Lets the user (or support/DPO staff) view current consent status and the
    // audit trail of GDPR actions taken on the account, for transparency (Art. 12).
    @GetMapping("/consent/{userId}")
    public ResponseEntity<?> getConsentAndAuditTrail(@PathVariable Long userId) {
        Optional<Map<String, Object>> status = gdprService.getConsentStatus(userId);
        if (status.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"User not found\"}");
        }
        Map<String, Object> body = new java.util.HashMap<>(status.get());
        body.put("auditTrail", gdprService.getAuditTrail(userId));
        return ResponseEntity.ok(body);
    }
}
