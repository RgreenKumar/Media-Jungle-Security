package com.VsmartEngine.MediaJungle.MailVerification;

import java.time.LocalDateTime;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.VsmartEngine.MediaJungle.LogManagement;
import com.VsmartEngine.MediaJungle.model.AddUser;
import com.VsmartEngine.MediaJungle.repository.AddUserRepository;
import com.VsmartEngine.MediaJungle.userregister.UserRegister;
import com.VsmartEngine.MediaJungle.userregister.UserRegisterRepository;

@RestController
public class VerificationController {
	
	@Autowired
    private EmailService emailService;

    @Autowired
    private UserRegisterRepository userregisterrepository;
    
    @Autowired
	private AddUserRepository adduserrepository;
    
<<<<<<< HEAD
    private final ConcurrentHashMap<String, String> verificationCodeStore = new ConcurrentHashMap<>();
    
    private static final Logger logger = LoggerFactory.getLogger(VerificationController.class);
     
    public ResponseEntity<String> sendCodewhileRegister(@RequestParam String email) {
        try {        
            // Check if the email is already registered
            Optional<UserRegister> existingUser = userregisterrepository.findByEmail(email);
            if (existingUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already registered.");
            }

            // Step 1: Generate a new verification code
            String code = UUID.randomUUID().toString().substring(0, 6);

            // Step 2: Save the code in the in-memory store
            verificationCodeStore.put(email, code);

            // Step 3: Send the verification code via email
            boolean isEmailSent = emailService.sendEmail(email, "Your Verification Code", "Verification Code: " + code);

            // Step 4: Check if the email was sent successfully
            if (isEmailSent) {
                // Return success response
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Verification code created and sent successfully.");
            } else {
                // If email fails to send, return 400 Bad Request
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Failed to send verification code. Please try again.");
            }
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error occurred: " + e.getMessage());
            logger.error("", e);
            // Return a generic error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request. Please try again later.");
=======
    @Autowired
    private com.VsmartEngine.MediaJungle.audit.AuditLogService auditLogService;
    
    private final ConcurrentHashMap<String, String> verificationCodeStore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> verificationExpiryStore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> verifiedEmails = new ConcurrentHashMap<>();
    
    private static final long OTP_VALIDITY_MS = 10 * 60 * 1000; // 10 minutes
    private static final long VERIFICATION_VALIDITY_MS = 15 * 60 * 1000; // 15 minutes
    private static final java.security.SecureRandom secureRandom = new java.security.SecureRandom();
    
    private static final Logger logger = LoggerFactory.getLogger(VerificationController.class);

    // Check if an email has been verified via OTP within validity window
    public boolean isEmailVerified(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String cleanEmail = email.trim().toLowerCase();
        Long expiry = verifiedEmails.get(cleanEmail);
        if (expiry != null && System.currentTimeMillis() < expiry) {
            return true;
        }
        verifiedEmails.remove(cleanEmail);
        return false;
    }

    // Consume verification token upon successful registration
    public void consumeEmailVerification(String email) {
        if (email != null) {
            verifiedEmails.remove(email.trim().toLowerCase());
        }
    }
     
    public ResponseEntity<String> sendCodewhileRegister(@RequestParam String email) {
        try {
            String cleanEmail = email != null ? email.trim().toLowerCase() : "";
            if (cleanEmail.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"success\": false, \"message\": \"Email address is required.\"}");
            }
            // Check if the email is already registered
            Optional<UserRegister> existingUser = userregisterrepository.findByEmail(cleanEmail);
            if (existingUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"success\": false, \"message\": \"Email is already registered. Please log in.\"}");
            }

            // Generate secure 6-digit numeric OTP
            String code = String.format("%06d", secureRandom.nextInt(1000000));

            // Save the code with 10-minute expiry
            verificationCodeStore.put(cleanEmail, code);
            verificationExpiryStore.put(cleanEmail, System.currentTimeMillis() + OTP_VALIDITY_MS);

            logger.info("=========================================");
            logger.info("VERIFICATION OTP FOR {}: {}", cleanEmail, code);
            logger.info("=========================================");
            System.out.println("=========================================");
            System.out.println("VERIFICATION OTP FOR " + cleanEmail + ": " + code);
            System.out.println("=========================================");

            // Send verification OTP via email
            boolean isEmailSent = emailService.sendEmail(cleanEmail, "Media Jungle - Your Verification Code", "Your Media Jungle verification code is: " + code + "\n\nThis OTP is valid for 10 minutes.");

            // ISO 27001 Audit Trail
            auditLogService.logAction(cleanEmail, "USER", "OTP_SENT", "AUTH", "/api/v2/send-code", "POST", null, "SUCCESS", "Verification OTP generated for: " + cleanEmail);

            if (isEmailSent) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("{\"success\": true, \"message\": \"Verification OTP sent successfully to your email ID. Please check your inbox or spam folder.\"}");
            } else {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("{\"success\": true, \"testOtp\": \"" + code + "\", \"message\": \"Verification code dispatched! (Local / Test OTP: " + code + ")\"}");
            }
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            logger.error("Error in sendCodewhileRegister", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"success\": false, \"message\": \"An error occurred while generating verification code.\"}");
>>>>>>> internship/main
        }
    }
      
    public ResponseEntity<String> sendCode(@RequestParam String email) {
<<<<<<< HEAD
        try {        
            // Check if the email is already registered
            Optional<UserRegister> existingUser = userregisterrepository.findByEmail(email);
            if (!existingUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is not exists.");
            }

            // Step 1: Generate a new verification code
            String code = UUID.randomUUID().toString().substring(0, 6);

            // Step 2: Save the code in the in-memory store
            verificationCodeStore.put(email, code);

            // Step 3: Send the verification code via email
            boolean isEmailSent = emailService.sendEmail(email, "Your Verification Code", "Verification Code: " + code);

            // Step 4: Check if the email was sent successfully
            if (isEmailSent) {
                // Return success response
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Verification code created and sent successfully.");
            } else {
                // If email fails to send, return 400 Bad Request
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Failed to send verification code. Please try again.");
            }
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error occurred: " + e.getMessage());
            logger.error("", e);
            // Return a generic error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request. Please try again later.");
        }
    }


    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        try {
            // Check if the email exists in the in-memory store
            if (verificationCodeStore.containsKey(email)) {
                String storedCode = verificationCodeStore.get(email);
                // Validate the code
                if (storedCode.equals(code)) {
                    // Code matches; remove it after successful verification
                    verificationCodeStore.remove(email);
                    return ResponseEntity.ok("{\"message\": \"Verification successful.\"}");
                } else {
                    // Code mismatch
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Invalid Verification Code \"}");
                }
            } else {
                // No code associated with the provided email
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"No verification code found for this email.\"}");
            }
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error during verification: " + e.getMessage());
            logger.error("", e);
            // Return generic server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"An error occurred during verification. Please try again later.\"}"+ e.getMessage());
=======
        try {
            String cleanEmail = email != null ? email.trim().toLowerCase() : "";
            if (cleanEmail.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"success\": false, \"message\": \"Email address is required.\"}");
            }
            Optional<UserRegister> existingUser = userregisterrepository.findByEmail(cleanEmail);
            if (!existingUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"success\": false, \"message\": \"Email does not exist.\"}");
            }

            String code = String.format("%06d", secureRandom.nextInt(1000000));
            verificationCodeStore.put(cleanEmail, code);
            verificationExpiryStore.put(cleanEmail, System.currentTimeMillis() + OTP_VALIDITY_MS);

            logger.info("=========================================");
            logger.info("PASSWORD RESET OTP FOR {}: {}", cleanEmail, code);
            logger.info("=========================================");

            boolean isEmailSent = emailService.sendEmail(cleanEmail, "Media Jungle - Password Reset Code", "Your password reset code is: " + code + "\n\nThis OTP is valid for 10 minutes.");

            auditLogService.logAction(cleanEmail, "USER", "OTP_SENT", "AUTH", "/api/v2/send-code/forgetpassword", "POST", null, "SUCCESS", "Password reset OTP sent to: " + cleanEmail);

            if (isEmailSent) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("{\"success\": true, \"message\": \"Password reset OTP sent successfully to your email ID.\"}");
            } else {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("{\"success\": true, \"testOtp\": \"" + code + "\", \"message\": \"Password reset code dispatched! (Local / Test OTP: " + code + ")\"}");
            }
        } catch (Exception e) {
            logger.error("Error in sendCode", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"success\": false, \"message\": \"An error occurred while processing your request.\"}");
        }
    }

    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        try {
            String cleanEmail = email != null ? email.trim().toLowerCase() : "";
            String cleanCode = code != null ? code.trim() : "";

            if (cleanEmail.isEmpty() || cleanCode.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"success\": false, \"message\": \"Email and OTP code are required.\"}");
            }

            if (verificationCodeStore.containsKey(cleanEmail)) {
                Long expiry = verificationExpiryStore.get(cleanEmail);
                if (expiry != null && System.currentTimeMillis() > expiry) {
                    verificationCodeStore.remove(cleanEmail);
                    verificationExpiryStore.remove(cleanEmail);
                    auditLogService.logAction(cleanEmail, "USER", "OTP_VERIFICATION_FAILURE", "AUTH", "/api/v2/verify-code", "POST", null, "FAILURE", "Expired OTP code for: " + cleanEmail);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"success\": false, \"message\": \"Verification code has expired. Please request a new OTP.\"}");
                }

                String storedCode = verificationCodeStore.get(cleanEmail);
                if (storedCode.equals(cleanCode)) {
                    verificationCodeStore.remove(cleanEmail);
                    verificationExpiryStore.remove(cleanEmail);

                    // Mark email as verified with a 15-minute validity window
                    verifiedEmails.put(cleanEmail, System.currentTimeMillis() + VERIFICATION_VALIDITY_MS);

                    // ISO 27001 Audit Trail
                    auditLogService.logAction(cleanEmail, "USER", "OTP_VERIFICATION_SUCCESS", "AUTH", "/api/v2/verify-code", "POST", null, "SUCCESS", "OTP verified successfully for: " + cleanEmail);

                    return ResponseEntity.ok("{\"success\": true, \"message\": \"Email verified successfully!\"}");
                } else {
                    auditLogService.logAction(cleanEmail, "USER", "OTP_VERIFICATION_FAILURE", "AUTH", "/api/v2/verify-code", "POST", null, "FAILURE", "Invalid OTP code provided for: " + cleanEmail);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"success\": false, \"message\": \"Invalid verification code.\"}");
                }
            } else {
                auditLogService.logAction(cleanEmail, "USER", "OTP_VERIFICATION_FAILURE", "AUTH", "/api/v2/verify-code", "POST", null, "FAILURE", "No OTP code found for: " + cleanEmail);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"success\": false, \"message\": \"No verification code found. Please request an OTP first.\"}");
            }
        } catch (Exception e) {
            logger.error("Error during verification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"success\": false, \"message\": \"An error occurred during verification.\"}");
>>>>>>> internship/main
        }
    }
}
