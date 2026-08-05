package com.VsmartEngine.MediaJungle.gdpr;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.VsmartEngine.MediaJungle.userregister.UserRegister;
import com.VsmartEngine.MediaJungle.userregister.UserRegisterRepository;

/**
 * GDPR-TASK-08/09/10/11: Central service implementing the data-subject rights required by the
 * GDPR for the "UserRegister" account (the primary personal-data record in this application).
 */
@Service
public class GdprService {

    @Autowired
    private UserRegisterRepository userRegisterRepository;

    @Autowired
    private GdprAuditLogRepository auditLogRepository;

    /**
     * GDPR-TASK-08: Right to Access (Art. 15) + Right to Data Portability (Art. 20).
     * Returns every piece of personal data the platform holds on the user, in a structured,
     * machine-readable (JSON-serialisable) format the user can download or transfer elsewhere.
     * NOTE: profile image bytes and password hash are intentionally excluded/redacted -
     * the password hash has no value to the data subject and must never be exported.
     */
    public Optional<Map<String, Object>> exportUserData(Long userId) {
        Optional<UserRegister> userOpt = userRegisterRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        UserRegister user = userOpt.get();

        Map<String, Object> export = new HashMap<>();
        export.put("id", user.getId());
        export.put("username", user.getUsername());
        export.put("email", user.getEmail());
        export.put("mobileNumber", user.getMobnum());
        export.put("registrationDate", user.getDate());
        export.put("consentGiven", user.isConsentGiven());
        export.put("consentTimestamp", user.getConsentTimestamp());
        export.put("marketingOptIn", user.isMarketingOptIn());
        export.put("termsVersion", user.getTermsVersion());
        export.put("favoriteAudioIds", user.getFavoriteAudioIds());
        export.put("favoriteVideoIds", user.getFavoriteVideosIds());
        export.put("watchLaterIds", user.getWatchlaterIds());
        export.put("hasProfilePicture", user.getProfile() != null && user.getProfile().length > 0);
        // Password hash and raw payment-gateway identifiers deliberately omitted from the export.

        logAction(userId, "DATA_EXPORT", "user", "User downloaded their personal data export.");
        return Optional.of(export);
    }

    /**
     * GDPR-TASK-09: Right to Erasure / "right to be forgotten" (Art. 17).
     * Anonymises (rather than hard-deletes) the account row so that referential integrity with
     * subscriptions/payment history/notifications is preserved for legal/financial record-keeping
     * (an allowed exception under Art. 17(3)(b)-(e)), while all directly identifying personal data
     * is irreversibly scrubbed. A random placeholder replaces the email/username/phone so the row
     * can no longer be linked back to the individual.
     */
    public boolean eraseUserData(Long userId) {
        Optional<UserRegister> userOpt = userRegisterRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        UserRegister user = userOpt.get();

        String anonymousTag = "erased-" + UUID.randomUUID();
        user.setUsername(anonymousTag);
        user.setEmail(anonymousTag + "@deleted.local");
        user.setMobnum(null);
        user.setPassword(new BCryptPasswordEncoder().encode(UUID.randomUUID().toString()));
        user.setProfile(null);
        user.setConsentGiven(false);
        user.setMarketingOptIn(false);
        user.getFavoriteAudioIds().clear();
        user.getFavoriteVideosIds().clear();
        user.getWatchlaterIds().clear();

        userRegisterRepository.save(user);
        logAction(userId, "DATA_ERASURE", "user", "User exercised the right to erasure; personal fields anonymised.");
        return true;
    }

    /**
     * GDPR-TASK-10: Right to withdraw consent at any time (Art. 7(3)), and to grant/refine
     * granular consent (e.g. marketing) after registration.
     */
    public Optional<UserRegister> updateConsent(Long userId, boolean consentGiven, boolean marketingOptIn) {
        Optional<UserRegister> userOpt = userRegisterRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        UserRegister user = userOpt.get();
        user.setConsentGiven(consentGiven);
        user.setMarketingOptIn(marketingOptIn);
        user.setConsentTimestamp(LocalDateTime.now());
        UserRegister saved = userRegisterRepository.save(user);

        String action = consentGiven ? "CONSENT_GRANTED" : "CONSENT_WITHDRAWN";
        logAction(userId, action, "user", "marketingOptIn=" + marketingOptIn);
        return Optional.of(saved);
    }

    /**
     * GDPR-TASK-15 support: lightweight consent-status lookup that does NOT write an audit
     * entry (unlike exportUserData), since merely viewing your own consent status is not itself
     * a reportable data-subject-rights action.
     */
    public Optional<Map<String, Object>> getConsentStatus(Long userId) {
        Optional<UserRegister> userOpt = userRegisterRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        UserRegister user = userOpt.get();
        Map<String, Object> status = new HashMap<>();
        status.put("consentGiven", user.isConsentGiven());
        status.put("marketingOptIn", user.isMarketingOptIn());
        status.put("consentTimestamp", user.getConsentTimestamp());
        status.put("termsVersion", user.getTermsVersion());
        return Optional.of(status);
    }

    public List<GdprAuditLog> getAuditTrail(Long userId) {
        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    /**
     * GDPR-TASK-11: Every data-subject-rights action is written to the audit trail so the
     * controller can demonstrate compliance on request (accountability, GDPR Art. 5(2)).
     */
    private void logAction(Long userId, String action, String performedBy, String details) {
        auditLogRepository.save(new GdprAuditLog(userId, action, performedBy, details));
    }
}
