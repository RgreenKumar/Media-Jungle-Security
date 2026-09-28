package com.VsmartEngine.MediaJungle.userregister;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.VsmartEngine.MediaJungle.security.PasswordSecurityUtil;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.VsmartEngine.MediaJungle.LogManagement;
import com.VsmartEngine.MediaJungle.MailVerification.EmailService;
import com.VsmartEngine.MediaJungle.compresser.ImageUtils;
import com.VsmartEngine.MediaJungle.notification.service.NotificationService;

@CrossOrigin()
@RestController
@RequestMapping("/api/v2/")
public class UserRegisterController {
    
    @Autowired
    private UserRegisterRepository userregisterrepository;

    @Autowired
    private com.VsmartEngine.MediaJungle.repository.AddUserRepository adduserrepository;
    
    @Autowired
    private JwtUtil jwtUtil; // Autowire JwtUtil

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenBlacklist tokenBlacklist;
    
	@Autowired
    private NotificationService notificationservice;  
	
	@Autowired
    private com.VsmartEngine.MediaJungle.audit.AuditLogService auditLogService;

    @Autowired
    private com.VsmartEngine.MediaJungle.upload.FileValidationService fileValidationService;

    @Autowired
    private com.VsmartEngine.MediaJungle.MailVerification.VerificationController verificationController;

    @Autowired
    private com.VsmartEngine.MediaJungle.security.PasswordPolicyService passwordPolicyService;

    @org.springframework.beans.factory.annotation.Value("${security.max-login-attempts:5}")
    private int maxLoginAttempts;

    @org.springframework.beans.factory.annotation.Value("${security.lock-duration-minutes:15}")
    private int lockDurationMinutes;

	private static final Logger logger = LoggerFactory.getLogger(UserRegisterController.class);
       
	   public ResponseEntity<?> register(
		        @RequestParam("username") String username,
		        @RequestParam("email") String email,
		        @RequestParam("password") String password,
		        @RequestParam("mobnum") String mobnum,
		        @RequestParam(value = "profile", required = false) MultipartFile profile) {
		    try {
		        String cleanEmail = email != null ? email.trim().toLowerCase() : "";
		        if (cleanEmail.isEmpty()) {
		            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
		                    .body("{\"message\": \"Email address is required.\"}");
		        }

		        // SOC 2 Control 6 & TC03, TC04: Prevent duplicate accounts
		        if (userregisterrepository.findByEmail(cleanEmail).isPresent()) {
		            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"message\": \"Email is already registered. Please log in.\"}");
		        }
		        if (userregisterrepository.findByUsername(username).isPresent()) {
		            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"message\": \"Username is already taken. Please choose another.\"}");
		        }

		        // SOC 2 CC6.2 & ISO 27001 Access Control: Verify Email OTP before registration
		        if (!verificationController.isEmailVerified(cleanEmail)) {
		            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
		                    .body("{\"message\": \"Email verification required. Please verify your email with OTP first.\"}");
		        }

		        // ===========================================
		        // Internship Security Enhancement
		        // Feature : Secure File Upload Validation
		        // ISO27001 Control : Secure File Handling
		        // ===========================================
		        // Validate MIME type, block executable files, and check max image size
		        if (profile != null && !profile.isEmpty()) {
		            ResponseEntity<?> validationResponse = fileValidationService.validateFile(profile, "IMAGE", username, "USER");
		            if (validationResponse != null) {
		                return validationResponse;
		            }
		        }

		        // SOC 2 Control 2: Strong Password Policy
		        com.VsmartEngine.MediaJungle.security.PasswordPolicyService.ValidationResult policyResult = passwordPolicyService.validate(password);
		        if (!policyResult.isValid()) {
		            return ResponseEntity.badRequest().body("{\"message\": \"" + policyResult.getErrorMessage() + "\"}");
		        }

		        // Encrypt the password using injected PasswordEncoder
		        String encodedPassword = PasswordSecurityUtil.encodeIfRaw(passwordEncoder, password);
		        // Set current date
		        LocalDate parsedDate = LocalDate.now();

		        // Create a new UserRegister object
		        UserRegister newRegister = new UserRegister();
		        newRegister.setUsername(username);
		        newRegister.setEmail(cleanEmail);
		        newRegister.setPassword(encodedPassword);
		        newRegister.setMobnum(mobnum);
		        newRegister.setDate(parsedDate);

		        // =======================================
		        // Internship Security Enhancement
		        // Feature: Role Based Access Control
		        // ISO27001 Control: Access Control
		        // =======================================
		        // RBAC: Force default USER role for public registration
		        // Prevent Privilege Escalation: Do not allow self-registration as ADMIN
		        newRegister.setRole(com.VsmartEngine.MediaJungle.security.UserRole.USER.name());

		        // Handle profile image
		        if (profile != null && !profile.isEmpty()) {
		            byte[] thumbnailBytes = ImageUtils.compressImage(profile.getBytes());
		            newRegister.setProfile(thumbnailBytes);
		        }

		        // Save the user to the repository
		        UserRegister savedUser = userregisterrepository.save(newRegister);

		        // Consume verification token after successful account creation
		        verificationController.consumeEmailVerification(cleanEmail);

		        // ISO27001 Audit Trail: Store user registration event
		        auditLogService.logAction(username, "USER", "USER_REGISTRATION", "AUTH", "/api/v2/register", "POST", null, "SUCCESS", "User registered successfully with email: " + cleanEmail);

		        // SOC 2 Control 1 & 12: Never return password hash in API response
		        savedUser.setPassword(null);
		        return ResponseEntity.ok(savedUser);

		    } catch (Exception e) {
		        // Handle exceptions
		    	logger.error("", e);
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body("{\"message\": \"An error occurred while registering the user: \"}" + e.getMessage());
		    }
		}
	
	
    public ResponseEntity<List<UserRegisterDTO>> getUsersRegisteredWithinLast15Days() {
        LocalDate startDate = LocalDate.now().minusDays(15);
        List<UserRegisterDTO> users  = userregisterrepository.findUsersRegisteredWithinLast15Days(startDate);
        if (!users.isEmpty()) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    
    public ResponseEntity<List<UserRegister>> getAllUser() {
        try {
            // Fetch all users from the repository
            List<UserRegister> getUser = userregisterrepository.findAll();
            // SOC 2 Control 1 & 12: Never expose password hashes in API responses
            getUser.forEach(u -> u.setPassword(null));
            
            // Return the list of users with 200 OK (empty list [] if no users)
            return ResponseEntity.ok(getUser);
        } catch (Exception e) {
            // Handle exceptions and return 500 Internal Server Error
        	logger.error("", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<UserRegister> getUserById(@PathVariable Long id) {
        Optional<UserRegister> userOptional = userregisterrepository.findById(id);
        if (userOptional.isPresent()) {
            UserRegister user = userOptional.get();
            // Check if the profile is present (not null and not empty)
            if (user.getProfile() != null && user.getProfile().length > 0) {
                byte[] images = ImageUtils.decompressImage(user.getProfile());
                user.setProfile(images);
            }
            // SOC 2 Control 1 & 12: Never expose password hashes in API responses
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
  
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        Optional<UserRegister> userOptional = userregisterrepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            // ISO27001 Audit Trail: Store failed login attempt
            auditLogService.logAction(email, "NONE", "LOGIN_FAILURE", "AUTH", "/api/v2/userlogin", "POST", null, "FAILURE", "Failed login attempt: User with email " + email + " not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Invalid email or password\"}");
        }
        UserRegister user = userOptional.get();

        // SOC 2 Control 6: Account Lifecycle Status Check
        String userStatus = user.getStatus() != null ? user.getStatus() : "ACTIVE";
        if ("SUSPENDED".equalsIgnoreCase(userStatus)) {
            auditLogService.logAction(user.getUsername(), user.getRole(), "LOGIN_REJECTED", "AUTH", "/api/v2/login", "POST", null, "FORBIDDEN", "Suspended user attempted login: " + email);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Account has been suspended. Please contact support."));
        }
        if ("DEACTIVATED".equalsIgnoreCase(userStatus)) {
            auditLogService.logAction(user.getUsername(), user.getRole(), "LOGIN_REJECTED", "AUTH", "/api/v2/login", "POST", null, "FORBIDDEN", "Deactivated user attempted login: " + email);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Account has been deactivated."));
        }
        if ("PENDING_VERIFICATION".equalsIgnoreCase(userStatus)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Account verification pending. Please verify your OTP."));
        }

        // ========================================
        // Internship Security Enhancement
        // Feature : Brute Force Protection
        // ISO27001 Control : Secure Authentication
        // ========================================
        // Prevent brute force attack: Check if account is currently locked
        if (user.isAccountLocked()) {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            if (user.getAccountLockedUntil() != null && now.isBefore(user.getAccountLockedUntil())) {
                // Prevent brute force attack: Return HTTP 423 LOCKED
                logger.warn("Prevent brute force attack: Account {} is locked until {}", user.getEmail(), user.getAccountLockedUntil());
                Map<String, Object> lockResponse = new HashMap<>();
                lockResponse.put("status", 423);
                lockResponse.put("message", "Your account has been temporarily locked due to multiple failed login attempts. Please try again after 15 minutes.");
                return ResponseEntity.status(HttpStatus.LOCKED).body(lockResponse);
            } else {
                // Auto unlock account: Lock duration expired
                user.setAccountLocked(false);
                user.setFailedLoginAttempts(0);
                user.setAccountLockedUntil(null);
                userregisterrepository.save(user);

                // ISO27001 Audit Trail: Store account unlock event
                auditLogService.logAction(user.getUsername(), user.getRole(), "ACCOUNT_UNLOCKED", "AUTH", "/api/v2/login", "POST", null, "SUCCESS", "Account automatically unlocked after lock duration expired");
                logger.info("Auto unlock account: Account {} unlocked automatically after lock duration expired", user.getEmail());
            }
        }

        if (!PasswordSecurityUtil.matches(passwordEncoder, password, user.getPassword())) {
            // Increase failed attempts
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            user.setLastFailedLogin(java.time.LocalDateTime.now());

            if (attempts >= maxLoginAttempts) {
                // Lock account: Threshold reached
                user.setAccountLocked(true);
                user.setAccountLockedUntil(java.time.LocalDateTime.now().plusMinutes(lockDurationMinutes));
                userregisterrepository.save(user);

                // ISO27001 Audit Trail: Record account lockout event
                auditLogService.logAction(user.getUsername(), user.getRole(), "ACCOUNT_LOCKED", "AUTH", "/api/v2/login", "POST", null, "LOCKED", "Account locked due to " + attempts + " consecutive failed login attempts");
                logger.warn("Lock account: Account {} locked after {} failed attempts", user.getEmail(), attempts);

                Map<String, Object> lockResponse = new HashMap<>();
                lockResponse.put("status", 423);
                lockResponse.put("message", "Your account has been temporarily locked due to multiple failed login attempts. Please try again after 15 minutes.");
                return ResponseEntity.status(HttpStatus.LOCKED).body(lockResponse);
            }

            userregisterrepository.save(user);

            // SOC 2 Audit Trail: Store failed login attempt (internal log only)
            auditLogService.logAction(user.getUsername(), user.getRole(), "LOGIN_FAILURE", "AUTH", "/api/v2/login", "POST", null, "FAILURE", "Failed login attempt (" + attempts + "/" + maxLoginAttempts + ") for email: " + email);

            // SOC 2 Control 3: Generic error message to prevent user enumeration
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Invalid email or password\"}");
        }

        // Reset counter after successful login
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        user.setAccountLockedUntil(null);
        userregisterrepository.save(user);
        
        // =======================================
        // Internship Security Enhancement
        // Feature: Role Based Access Control
        // ISO27001 Control: Access Control
        // =======================================
        // RBAC: Retrieve role from user entity
        String role = (user.getRole() != null && !user.getRole().isEmpty()) ? user.getRole() : com.VsmartEngine.MediaJungle.security.UserRole.USER.name();

        // SOC 2 Control 4: Multi-Factor Authentication Check
        if (user.isMfaEnabled()) {
            String mfaTempToken = jwtUtil.generateMfaTempToken(email, role, "USER", user.getId());
            auditLogService.logAction(user.getUsername(), role, "MFA_CHALLENGE_ISSUED", "AUTH", "/api/v2/login", "POST", null, "PENDING", "MFA required for user: " + email);
            Map<String, Object> mfaResponse = new HashMap<>();
            mfaResponse.put("mfaRequired", true);
            mfaResponse.put("tempToken", mfaTempToken);
            mfaResponse.put("userId", user.getId());
            mfaResponse.put("userType", "USER");
            mfaResponse.put("message", "Multi-factor authentication code required");
            return ResponseEntity.ok(mfaResponse);
        }

        String jwtToken = jwtUtil.generateToken(email, role);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", jwtToken);
        responseBody.put("message", "Login successful");
        responseBody.put("name", user.getUsername());
        responseBody.put("email", user.getEmail());
        responseBody.put("userId", user.getId());
        responseBody.put("role", role); // RBAC: Include role in login response
//        responseBody.put("profile", null); // Simply set image as null without loading it

        // ISO27001 Audit Trail: Store login event
        auditLogService.logAction(user.getUsername(), role, "LOGIN_SUCCESS", "AUTH", "/api/v2/login", "POST", null, "SUCCESS", "User logged in successfully");

        // Check if the user has an expiry date for subscription
        if (user.getPaymentId() != null && user.getPaymentId().getExpiryDate() != null) {
            LocalDate expdate = user.getPaymentId().getExpiryDate();
            LocalDate today = LocalDate.now();
            String plan = user.getPaymentId().getSubscriptionTitle();
            
            if (expdate.minusDays(1).equals(today)) {
                // Create notification and associate with user
                String heading = user.getUsername() + ", your " + plan + " subscription validity expires on " + expdate;
                try {
                    Long notifyId = notificationservice.createNotification(user.getUsername(), user.getEmail(), heading);
                    if (notifyId != null) {
                        notificationservice.notificationuser(notifyId, user.getId());
                    } else {
                        // Handle notification creation failure
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Failed to create notification\"}");
                    }
                } catch (Exception e) {
                    // Handle any exceptions during notification creation
                	logger.error("", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Error creating notification\"}");
                }
            }
        }
        // Successful login
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        tokenBlacklist.blacklistToken(token);
        System.out.println("Logged out successfully");

        // ISO27001 Audit Trail: Record user logout event
        auditLogService.logAction("USER", "USER", "LOGOUT", "AUTH", "/api/v2/userlogout", "POST", null, "SUCCESS", "User logged out successfully");

        return ResponseEntity.ok().body("Logged out successfully");
    }
    
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> loginRequest) {
        try {
            // Finding the user by email
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");
            if (email == null || email.trim().isEmpty() || password == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email and new password are required"));
            }
            String cleanEmail = email.trim().toLowerCase();

            // SOC 2 Control 6 & 7: Enforce OTP verification before allowing password reset
            if (!verificationController.isEmailVerified(cleanEmail)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Email verification required. Please verify your email with OTP first."));
            }

            Optional<UserRegister> userOptional = userregisterrepository.findByEmail(cleanEmail);

            // If the user doesn't exist, return 404 Not Found
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
            }

            UserRegister user = userOptional.get();

            // SOC 2 Control 2: Strong Password Policy
            com.VsmartEngine.MediaJungle.security.PasswordPolicyService.ValidationResult policyResult = passwordPolicyService.validate(password);
            if (!policyResult.isValid()) {
                return ResponseEntity.badRequest().body(Map.of("message", policyResult.getErrorMessage()));
            }

            String encodedPassword = PasswordSecurityUtil.encodeIfRaw(passwordEncoder, password);

            // Update the user password
            user.setPassword(encodedPassword);

            // Save the updated user
            UserRegister savedUser = userregisterrepository.save(user);

            // Consume verification token
            verificationController.consumeEmailVerification(cleanEmail);

            Long userId = savedUser.getId();
            String name = savedUser.getUsername();
            String heading = name + " successfully changed your password";

            // Create notification and associate with user
            Long notifyId = notificationservice.createNotification(name, cleanEmail, heading);
            
            if (notifyId != null) {
                notificationservice.notificationuser(notifyId, userId);
            }

            // ISO27001 Audit Trail: Store password reset event
            auditLogService.logAction(savedUser.getUsername(), savedUser.getRole(), "PASSWORD_RESET", "AUTH", "/api/v2/userresetPassword", "POST", null, "SUCCESS", "Password reset successfully for email: " + cleanEmail);

            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));

        } catch (Exception e) {
            logger.error("Error in resetPassword", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An error occurred while resetting password"));
        }
    }
    
    

    public ResponseEntity<String> updateUserr(
            @PathVariable Long userId,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "mobnum", required = false) String mobnum,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "profile", required = false) MultipartFile profile) {

        try {
            // Retrieve existing user data from the repository
            Optional<UserRegister> optionalUserRegister = userregisterrepository.findById(userId);
            if (!optionalUserRegister.isPresent()) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            UserRegister existingUser = optionalUserRegister.get();
            // Apply partial updates to the existing user data if provided
            if (username != null) {
                existingUser.setUsername(username);
            }
            if (email != null) {
                existingUser.setEmail(email);
            }
            if (mobnum != null) {
                existingUser.setMobnum(mobnum);
            }
            if (password != null && !password.trim().isEmpty()) {
                com.VsmartEngine.MediaJungle.security.PasswordPolicyService.ValidationResult policyResult = passwordPolicyService.validate(password.trim());
                if (!policyResult.isValid()) {
                    return ResponseEntity.badRequest().body("{\"message\": \"" + policyResult.getErrorMessage() + "\"}");
                }
            	String encodedPassword = PasswordSecurityUtil.encodeIfRaw(passwordEncoder, password.trim());
	            existingUser.setPassword(encodedPassword);
	        }
	        
            if (profile != null && !profile.isEmpty()) {
                byte[] thumbnailBytes = ImageUtils.compressImage(profile.getBytes());
                existingUser.setProfile(thumbnailBytes);
            }

            // Save the updated user data back to the repository
            userregisterrepository.save(existingUser);
            return new ResponseEntity<>("User details updated successfully", HttpStatus.OK);
        } catch (IOException e) {
        	logger.error("", e);
            return new ResponseEntity<>("Error processing profile image", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
        	logger.error("", e);
            return new ResponseEntity<>("Error updating user details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // =========================================================================
    // ADMIN CRUD: ADD REGISTERED USER
    // =========================================================================
    @PostMapping("/admin/registered-user")
    public ResponseEntity<?> adminAddUser(
            @RequestBody Map<String, String> userData,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or missing token"));
            }
            String role = jwtUtil.getRoleFromToken(token);
            if (role == null || (!"ADMIN".equalsIgnoreCase(role) && !"SUBADMIN".equalsIgnoreCase(role))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Administrator privileges required"));
            }

            String username = userData.get("username");
            String email = userData.get("email");
            String password = userData.get("password");
            String mobnum = userData.get("mobnum");
            String targetRole = userData.get("role");

            if (username == null || username.trim().isEmpty() || email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Username and email are required"));
            }

            String cleanEmail = email.trim().toLowerCase();
            if (userregisterrepository.findByEmail(cleanEmail).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Email is already registered"));
            }

            UserRegister newUser = new UserRegister();
            newUser.setUsername(username.trim());
            newUser.setEmail(cleanEmail);
            newUser.setMobnum(mobnum != null ? mobnum.trim() : "");
            newUser.setDate(LocalDate.now());

            String userRole = (targetRole != null && !targetRole.trim().isEmpty()) ? targetRole.trim().toUpperCase() : "USER";
            newUser.setRole(userRole);

            String rawPassword = (password != null && !password.trim().isEmpty()) ? password.trim() : "Default@123";
            com.VsmartEngine.MediaJungle.security.PasswordPolicyService.ValidationResult policyResult = passwordPolicyService.validate(rawPassword);
            if (!policyResult.isValid()) {
                return ResponseEntity.badRequest().body(Map.of("message", policyResult.getErrorMessage()));
            }
            newUser.setPassword(PasswordSecurityUtil.encodeIfRaw(passwordEncoder, rawPassword));

            UserRegister saved = userregisterrepository.save(newUser);

            String adminUser = jwtUtil.getUsernameFromToken(token);
            auditLogService.logAction(adminUser != null ? adminUser : "ADMIN", role, "ADMIN_USER_ADDED", "ADMIN", "/api/v2/admin/registered-user", "POST", null, "SUCCESS", "Administrator added user: " + saved.getUsername() + " (" + cleanEmail + ")");

            // SOC 2 Control 1 & 12: Never return password hash in API response
            saved.setPassword(null);
            return ResponseEntity.ok(Map.of("message", "User created successfully", "user", saved));
        } catch (Exception e) {
            logger.error("Error in adminAddUser", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error adding user: " + e.getMessage()));
        }
    }

    // =========================================================================
    // ADMIN CRUD: UPDATE REGISTERED USER
    // =========================================================================
    @PutMapping("/admin/registered-user/{id}")
    public ResponseEntity<?> adminUpdateUser(
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> userData,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or missing token"));
            }
            String role = jwtUtil.getRoleFromToken(token);
            if (role == null || (!"ADMIN".equalsIgnoreCase(role) && !"SUBADMIN".equalsIgnoreCase(role))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Administrator privileges required"));
            }

            Optional<UserRegister> optionalUser = userregisterrepository.findById(id);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found with ID: " + id));
            }

            UserRegister user = optionalUser.get();
            if (userData.containsKey("username") && userData.get("username") != null) {
                user.setUsername(userData.get("username").trim());
            }
            if (userData.containsKey("email") && userData.get("email") != null) {
                user.setEmail(userData.get("email").trim().toLowerCase());
            }
            if (userData.containsKey("mobnum") && userData.get("mobnum") != null) {
                user.setMobnum(userData.get("mobnum").trim());
            }
            if (userData.containsKey("role") && userData.get("role") != null) {
                user.setRole(userData.get("role").trim().toUpperCase());
            }
            if (userData.containsKey("password") && userData.get("password") != null && !userData.get("password").trim().isEmpty()) {
                String rawPassword = userData.get("password").trim();
                com.VsmartEngine.MediaJungle.security.PasswordPolicyService.ValidationResult updatePolicyResult = passwordPolicyService.validate(rawPassword);
                if (!updatePolicyResult.isValid()) {
                    return ResponseEntity.badRequest().body(Map.of("message", updatePolicyResult.getErrorMessage()));
                }
                user.setPassword(PasswordSecurityUtil.encodeIfRaw(passwordEncoder, rawPassword));
            }

            userregisterrepository.save(user);

            String adminUser = jwtUtil.getUsernameFromToken(token);
            auditLogService.logAction(adminUser != null ? adminUser : "ADMIN", role, "ADMIN_USER_UPDATED", "ADMIN", "/api/v2/admin/registered-user/" + id, "PUT", null, "SUCCESS", "Administrator updated user: " + user.getUsername());

            // SOC 2 Control 1 & 12: Never return password hash in API response
            user.setPassword(null);
            return ResponseEntity.ok(Map.of("message", "User details updated successfully", "user", user));
        } catch (Exception e) {
            logger.error("Error in adminUpdateUser", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error updating user: " + e.getMessage()));
        }
    }

    // =========================================================================
    // ADMIN CRUD: DELETE REGISTERED USER
    // =========================================================================
    @DeleteMapping("/admin/registered-user/{id}")
    public ResponseEntity<?> adminDeleteUser(
            @PathVariable("id") Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or missing token"));
            }
            String role = jwtUtil.getRoleFromToken(token);
            if (role == null || (!"ADMIN".equalsIgnoreCase(role) && !"SUBADMIN".equalsIgnoreCase(role))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Administrator privileges required"));
            }

            Optional<UserRegister> optionalUser = userregisterrepository.findById(id);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found with ID: " + id));
            }

            String username = optionalUser.get().getUsername();
            userregisterrepository.deleteById(id);

            String adminUser = jwtUtil.getUsernameFromToken(token);
            auditLogService.logAction(adminUser != null ? adminUser : "ADMIN", role, "ADMIN_USER_DELETED", "ADMIN", "/api/v2/admin/registered-user/" + id, "DELETE", null, "SUCCESS", "Administrator deleted user: " + username + " (ID: " + id + ")");

            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            logger.error("Error in adminDeleteUser", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error deleting user: " + e.getMessage()));
        }
    }

    // =========================================================================
    // ADMIN CRUD: DELETE ALL REGISTERED USERS
    // =========================================================================
    @DeleteMapping("/admin/registered-user/delete-all")
    public ResponseEntity<?> adminDeleteAllUsers(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or missing token"));
            }
            String role = jwtUtil.getRoleFromToken(token);
            if (role == null || (!"ADMIN".equalsIgnoreCase(role) && !"SUBADMIN".equalsIgnoreCase(role))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Administrator privileges required"));
            }

            long count = userregisterrepository.count();
            userregisterrepository.deleteAll();

            String adminUser = jwtUtil.getUsernameFromToken(token);
            auditLogService.logAction(adminUser != null ? adminUser : "ADMIN", role, "ADMIN_ALL_USERS_DELETED", "ADMIN", "/api/v2/admin/registered-user/delete-all", "DELETE", null, "SUCCESS", "Administrator deleted all registered users (count: " + count + ")");

            return ResponseEntity.ok(Map.of("message", "All registered users deleted successfully", "deletedCount", count));
        } catch (Exception e) {
            logger.error("Error in adminDeleteAllUsers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error deleting all users: " + e.getMessage()));
        }
    }

    // =========================================================================
    // ADMIN CRUD: UNIFIED DELETE ALL USERS (EXCEPT PRIMARY ADMIN)
    // =========================================================================
    @DeleteMapping("/users/delete-all")
    public ResponseEntity<?> deleteAllUsersUnified(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or missing token"));
            }
            String role = jwtUtil.getRoleFromToken(token);
            if (role == null || (!"ADMIN".equalsIgnoreCase(role) && !"SUBADMIN".equalsIgnoreCase(role))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Administrator privileges required"));
            }

            long regCount = userregisterrepository.count();
            userregisterrepository.deleteAll();

            String currentAdmin = jwtUtil.getUsernameFromToken(token);
            List<com.VsmartEngine.MediaJungle.model.AddUser> allStaff = adduserrepository.findAll();
            long staffDeleted = 0;
            for (com.VsmartEngine.MediaJungle.model.AddUser staff : allStaff) {
                if (!"Hari".equalsIgnoreCase(staff.getUsername()) && !"admin".equalsIgnoreCase(staff.getUsername()) && !staff.getUsername().equalsIgnoreCase(currentAdmin)) {
                    adduserrepository.delete(staff);
                    staffDeleted++;
                }
            }

            String adminUser = currentAdmin != null ? currentAdmin : "ADMIN";
            auditLogService.logAction(adminUser, role, "ADMIN_ALL_USERS_DELETED", "ADMIN", "/api/v2/users/delete-all", "DELETE", null, "SUCCESS", "Administrator deleted all registered users (" + regCount + ") and non-primary staff (" + staffDeleted + ")");

            return ResponseEntity.ok(Map.of(
                    "message", "All users deleted successfully",
                    "registeredUsersDeleted", regCount,
                    "staffUsersDeleted", staffDeleted
            ));
        } catch (Exception e) {
            logger.error("Error in deleteAllUsersUnified", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error deleting users: " + e.getMessage()));
        }
    }

    // =========================================================================
    // SOC 2 Control 3: ADMIN UNLOCK USER ACCOUNT
    // =========================================================================
    @PostMapping("/admin/registered-user/{id}/unlock")
    public ResponseEntity<?> adminUnlockUser(
            @PathVariable("id") Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or missing token"));
            }
            String role = jwtUtil.getRoleFromToken(token);
            if (role == null || (!"ADMIN".equalsIgnoreCase(role) && !"SUBADMIN".equalsIgnoreCase(role))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Administrator privileges required"));
            }

            Optional<UserRegister> optionalUser = userregisterrepository.findById(id);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found with ID: " + id));
            }

            UserRegister user = optionalUser.get();
            user.setAccountLocked(false);
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(null);
            userregisterrepository.save(user);

            String adminUser = jwtUtil.getUsernameFromToken(token);
            auditLogService.logAction(adminUser != null ? adminUser : "ADMIN", role, "ACCOUNT_UNLOCKED_BY_ADMIN", "ADMIN", "/api/v2/admin/registered-user/" + id + "/unlock", "POST", null, "SUCCESS", "Administrator unlocked account for user: " + user.getUsername() + " (" + user.getEmail() + ")");

            return ResponseEntity.ok(Map.of("message", "User account unlocked successfully", "userId", id));
        } catch (Exception e) {
            logger.error("Error in adminUnlockUser", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error unlocking user: " + e.getMessage()));
        }
    }

    // =========================================================================
    // SOC 2 Control 6: USER SELF DE-REGISTRATION / ACCOUNT DELETION
    // =========================================================================
    @DeleteMapping("/user/delete-account")
    public ResponseEntity<?> selfDeleteAccount(
            @RequestHeader("Authorization") String token,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired token"));
            }

            String email = jwtUtil.getUsernameFromToken(token);
            Optional<UserRegister> userOpt = userregisterrepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User account not found"));
            }

            UserRegister user = userOpt.get();

            // Blacklist the session token immediately
            tokenBlacklist.blacklistToken(token);

            // Anonymize and deactivate user account
            user.setStatus("DEACTIVATED");
            userregisterrepository.save(user);

            auditLogService.logAction(user.getUsername(), user.getRole(), "USER_DE_REGISTRATION", "AUTH", "/api/v2/user/delete-account", "DELETE", null, "SUCCESS", "User voluntarily closed and deactivated their account: " + email);

            return ResponseEntity.ok(Map.of("message", "Account successfully deactivated"));
        } catch (Exception e) {
            logger.error("Error in selfDeleteAccount", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error de-registering account"));
        }
    }

    // =========================================================================
    // SOC 2 Control 6: ADMIN ACCOUNT STATUS MANAGEMENT (ACTIVE/SUSPENDED/DEACTIVATED)
    // =========================================================================
    @PatchMapping("/admin/registered-user/{id}/status")
    public ResponseEntity<?> adminUpdateUserStatus(
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> statusBody,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or missing token"));
            }
            String role = jwtUtil.getRoleFromToken(token);
            if (role == null || (!"ADMIN".equalsIgnoreCase(role) && !"SUBADMIN".equalsIgnoreCase(role))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Administrator privileges required"));
            }

            String newStatus = statusBody.get("status");
            if (newStatus == null || (!newStatus.equalsIgnoreCase("ACTIVE") && !newStatus.equalsIgnoreCase("SUSPENDED") && !newStatus.equalsIgnoreCase("DEACTIVATED"))) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid status. Allowed values: ACTIVE, SUSPENDED, DEACTIVATED"));
            }

            Optional<UserRegister> optionalUser = userregisterrepository.findById(id);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found with ID: " + id));
            }

            UserRegister user = optionalUser.get();
            user.setStatus(newStatus.toUpperCase());
            userregisterrepository.save(user);

            String adminUser = jwtUtil.getUsernameFromToken(token);
            auditLogService.logAction(adminUser != null ? adminUser : "ADMIN", role, "ADMIN_USER_STATUS_UPDATED", "ADMIN", "/api/v2/admin/registered-user/" + id + "/status", "PATCH", null, "SUCCESS", "Administrator set status to " + newStatus.toUpperCase() + " for user: " + user.getUsername());

            user.setPassword(null);
            return ResponseEntity.ok(Map.of("message", "User status updated to " + newStatus.toUpperCase(), "user", user));
        } catch (Exception e) {
            logger.error("Error in adminUpdateUserStatus", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error updating status: " + e.getMessage()));
        }
    }
}