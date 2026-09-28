package com.VsmartEngine.MediaJungle.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
=======
import org.springframework.security.crypto.password.PasswordEncoder;
import com.VsmartEngine.MediaJungle.security.PasswordSecurityUtil;
>>>>>>> internship/main
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
<<<<<<< HEAD
=======
import org.springframework.web.bind.annotation.RequestParam;
>>>>>>> internship/main

import com.VsmartEngine.MediaJungle.Container.VideoContainerController;
import com.VsmartEngine.MediaJungle.model.AddUser;
import com.VsmartEngine.MediaJungle.model.License;
import com.VsmartEngine.MediaJungle.model.UserListWithStatus;
import com.VsmartEngine.MediaJungle.notification.service.NotificationService;
import com.VsmartEngine.MediaJungle.repository.AddUserRepository;
import com.VsmartEngine.MediaJungle.repository.licenseRepository;
import com.VsmartEngine.MediaJungle.userregister.JwtUtil;
import com.VsmartEngine.MediaJungle.userregister.TokenBlacklist;


<<<<<<< HEAD
=======
import org.springframework.security.access.prepost.PreAuthorize;

>>>>>>> internship/main
@Controller
public class AddUserController {

	@Autowired
	private AddUserRepository adduserrepository;
	
    @Autowired
<<<<<<< HEAD
=======
    private PasswordEncoder passwordEncoder;
	
    @Autowired
    private com.VsmartEngine.MediaJungle.security.PasswordPolicyService passwordPolicyService;
	
    @Autowired
>>>>>>> internship/main
    private licenseRepository licenseRepository;
    
    @Autowired
    private JwtUtil jwtUtil; // Autowire JwtUtil
    
    @Autowired
    private TokenBlacklist tokenBlacklist;
    
    @Autowired
    private NotificationService notificationservice;
    
<<<<<<< HEAD
=======
    @Autowired
    private com.VsmartEngine.MediaJungle.audit.AuditLogService auditLogService;

    @Autowired
    private com.VsmartEngine.MediaJungle.MailVerification.VerificationController verificationController;

    @org.springframework.beans.factory.annotation.Value("${security.max-login-attempts:5}")
    private int maxLoginAttempts;

    @org.springframework.beans.factory.annotation.Value("${security.lock-duration-minutes:15}")
    private int lockDurationMinutes;
    
>>>>>>> internship/main
    private static final Logger logger = LoggerFactory.getLogger(AddUserController.class);

   
    public ResponseEntity<?> adminRegister(@RequestBody AddUser data) {
		try {
	        
	        Optional<AddUser> adduser = adduserrepository.findByEmail(data.getEmail());
	        if(adduser.isPresent()) {
	        	return ResponseEntity.badRequest().body("Email is already registered.");
	        }
	        
			Optional<AddUser> userOptional = adduserrepository.findByRole("ADMIN");
			
			 // If an ADMIN role already exists, set the role to SUBADMIN, otherwise set it to ADMIN
	        if (userOptional.isPresent()) {
	            data.setRole("SUBADMIN");
	        } else {
	            data.setRole("ADMIN");
	        }
<<<<<<< HEAD
            // Example: Encrypting password before saving (if AddUser has a password field)
             BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
             String hashedPassword = passwordEncoder.encode(data.getPassword());
             data.setPassword(hashedPassword);
             BCryptPasswordEncoder passwordEnc = new BCryptPasswordEncoder();
             String hashedPass = passwordEncoder.encode(data.getConfirmPassword());
             data.setConfirmPassword(hashedPass);
             
=======
            // SOC 2 Control 2: Strong Password Policy
            com.VsmartEngine.MediaJungle.security.PasswordPolicyService.ValidationResult policyResult = passwordPolicyService.validate(data.getPassword());
            if (!policyResult.isValid()) {
                return ResponseEntity.badRequest().body("{\"message\": \"" + policyResult.getErrorMessage() + "\"}");
            }

            // Encrypting password before saving using centralized PasswordEncoder
            String hashedPassword = PasswordSecurityUtil.encodeIfRaw(passwordEncoder, data.getPassword());
            data.setPassword(hashedPassword);
            data.setConfirmPassword(null);
>>>>>>> internship/main

			adduserrepository.save(data);
			return ResponseEntity.ok("success");
        } catch (Exception e) {
        	logger.error("", e);
        	 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed");
        }
	
    }
	
<<<<<<< HEAD
=======
	// =======================================
	// Internship Security Enhancement
	// Feature: Role Based Access Control
	// ISO27001 Control: Access Control
	// =======================================
	// RBAC: Only Admin Allowed to add new users/subadmins
	@PreAuthorize("hasRole('ADMIN')")
>>>>>>> internship/main
    public ResponseEntity<?> addUser(@RequestBody AddUser data, @RequestHeader("Authorization") String token) {
        try {
            // Extract role from the token
            String role = jwtUtil.getRoleFromToken(token);
            System.out.println("role"+ role);

            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\": \"Only admin can add subadmins\"}");
            }
	        
	        Optional<AddUser> adduser = adduserrepository.findByEmail(data.getEmail());
	        if(adduser.isPresent()) {
	        	return ResponseEntity.badRequest().body("Email is already registered.");
	        }

<<<<<<< HEAD
            Optional<AddUser> userOptional = adduserrepository.findByRole("ADMIN");

            // If an ADMIN role already exists, set the role to SUBADMIN, otherwise set it to ADMIN
            if (userOptional.isPresent()) {
                data.setRole("SUBADMIN");
            } else {
                data.setRole("ADMIN");
            }

            // Encrypting password before saving
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(data.getPassword());
            data.setPassword(hashedPassword);
            String hashedConfirmPassword = passwordEncoder.encode(data.getConfirmPassword());
            data.setConfirmPassword(hashedConfirmPassword);

            adduserrepository.save(data);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
        	logger.error("", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed");
=======
            // Allow custom role if provided, otherwise default to SUBADMIN if ADMIN exists
            if (data.getRole() == null || data.getRole().trim().isEmpty()) {
                Optional<AddUser> userOptional = adduserrepository.findByRole("ADMIN");
                if (userOptional.isPresent()) {
                    data.setRole("SUBADMIN");
                } else {
                    data.setRole("ADMIN");
                }
            } else {
                data.setRole(data.getRole().toUpperCase());
            }

            // SOC 2 Control 2: Strong Password Policy
            String rawPassword = (data.getPassword() != null && !data.getPassword().isEmpty()) ? data.getPassword() : "Default@123";
            com.VsmartEngine.MediaJungle.security.PasswordPolicyService.ValidationResult addPolicyResult = passwordPolicyService.validate(rawPassword);
            if (!addPolicyResult.isValid()) {
                return ResponseEntity.badRequest().body(Map.of("message", addPolicyResult.getErrorMessage()));
            }

            // Encrypting password before saving
            String hashedPassword = PasswordSecurityUtil.encodeIfRaw(passwordEncoder, rawPassword);
            data.setPassword(hashedPassword);
            data.setConfirmPassword(null);

            AddUser saved = adduserrepository.save(data);

            // ISO 27001 Audit Trail
            auditLogService.logAction(data.getUsername(), data.getRole(), "ADMIN_USER_ADDED", "ADMIN", "/api/v2/AddUser", "POST", null, "SUCCESS", "Administrator added user: " + data.getUsername() + " (" + data.getRole() + ")");

            // Sanitize password before returning
            saved.setPassword(null);
            saved.setConfirmPassword(null);

            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "User created successfully");
            resp.put("user", saved);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
        	logger.error("", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Failed to add user: " + e.getMessage() + "\"}");
>>>>>>> internship/main
        }
    }
    
	

	public ResponseEntity<?> loginadmin(@RequestBody Map<String, String> loginRequest) {
	    try {
	        String username = loginRequest.get("username");
<<<<<<< HEAD
=======
	        if (username == null) {
	            username = loginRequest.get("userName");
	        }
>>>>>>> internship/main
	        String password = loginRequest.get("password");
	        Optional<AddUser> userOptional = adduserrepository.findByUsername(username);

	        if (userOptional.isEmpty()) {
<<<<<<< HEAD
	        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"User not found\"}");
	        }

	        AddUser user = userOptional.get();
	        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	        if (!passwordEncoder.matches(password, user.getPassword())) {
	        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Incorrect password\"}");
	        }
	        
	        String role = user.getRole(); // Get user role
	        String jwtToken = jwtUtil.generateToken(username,role); // Use jwtUtil
=======
	            // SOC 2 Audit Trail: Store failed admin login attempt
	            auditLogService.logAction(username, "ADMIN", "LOGIN_FAILURE", "AUTH", "/api/v2/adminlogin", "POST", null, "FAILURE", "Failed admin login attempt: User " + username + " not found");
	        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Invalid username or password\"}");
	        }

	        AddUser user = userOptional.get();

            // SOC 2 Control 6: Account Status Lifecycle Check
            String staffStatus = user.getStatus() != null ? user.getStatus() : "ACTIVE";
            if ("SUSPENDED".equalsIgnoreCase(staffStatus)) {
                auditLogService.logAction(user.getUsername(), user.getRole(), "LOGIN_REJECTED", "AUTH", "/api/v2/adminlogin", "POST", null, "FORBIDDEN", "Suspended staff attempted login: " + username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Staff account has been suspended. Please contact administrator."));
            }
            if ("DEACTIVATED".equalsIgnoreCase(staffStatus)) {
                auditLogService.logAction(user.getUsername(), user.getRole(), "LOGIN_REJECTED", "AUTH", "/api/v2/adminlogin", "POST", null, "FORBIDDEN", "Deactivated staff attempted login: " + username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Staff account has been deactivated."));
            }

            // SOC 2 Control 3: Brute Force Protection - Account Lockout Check
            if (user.isAccountLocked()) {
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                if (user.getAccountLockedUntil() != null && now.isBefore(user.getAccountLockedUntil())) {
                    logger.warn("Admin brute force protection: Account {} is locked until {}", user.getUsername(), user.getAccountLockedUntil());
                    Map<String, Object> lockResponse = new HashMap<>();
                    lockResponse.put("status", 423);
                    lockResponse.put("message", "Your account has been temporarily locked due to multiple failed login attempts. Please try again after 15 minutes.");
                    return ResponseEntity.status(HttpStatus.LOCKED).body(lockResponse);
                } else {
                    // Auto unlock after lock duration expired
                    user.setAccountLocked(false);
                    user.setFailedLoginAttempts(0);
                    user.setAccountLockedUntil(null);
                    adduserrepository.save(user);

                    auditLogService.logAction(user.getUsername(), user.getRole(), "ACCOUNT_UNLOCKED", "AUTH", "/api/v2/adminlogin", "POST", null, "SUCCESS", "Admin account automatically unlocked after lock duration expired");
                }
            }

	        if (!PasswordSecurityUtil.matches(passwordEncoder, password, user.getPassword())) {
                int attempts = user.getFailedLoginAttempts() + 1;
                user.setFailedLoginAttempts(attempts);
                user.setLastFailedLogin(java.time.LocalDateTime.now());

                if (attempts >= maxLoginAttempts) {
                    user.setAccountLocked(true);
                    user.setAccountLockedUntil(java.time.LocalDateTime.now().plusMinutes(lockDurationMinutes));
                    adduserrepository.save(user);

                    auditLogService.logAction(user.getUsername(), user.getRole(), "ACCOUNT_LOCKED", "AUTH", "/api/v2/adminlogin", "POST", null, "LOCKED", "Admin account locked due to " + attempts + " consecutive failed login attempts");
                    logger.warn("Lock account: Admin {} locked after {} failed attempts", user.getUsername(), attempts);

                    Map<String, Object> lockResponse = new HashMap<>();
                    lockResponse.put("status", 423);
                    lockResponse.put("message", "Your account has been temporarily locked due to multiple failed login attempts. Please try again after 15 minutes.");
                    return ResponseEntity.status(HttpStatus.LOCKED).body(lockResponse);
                }

                adduserrepository.save(user);

	            // SOC 2 Audit Trail: Store failed admin login attempt
	            auditLogService.logAction(username, user.getRole(), "LOGIN_FAILURE", "AUTH", "/api/v2/adminlogin", "POST", null, "FAILURE", "Failed admin login attempt: Incorrect password for user " + username);
	        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Invalid username or password\"}");
	        }

            // Reset counter after successful login
            user.setFailedLoginAttempts(0);
            user.setAccountLocked(false);
            user.setAccountLockedUntil(null);
            adduserrepository.save(user);
	        
	        String role = user.getRole(); // Get user role
	        if (role == null || (!"ADMIN".equalsIgnoreCase(role) && !"SUBADMIN".equalsIgnoreCase(role))) {
	            auditLogService.logAction(username, role != null ? role : "UNKNOWN", "LOGIN_FAILURE", "AUTH", "/api/v2/adminlogin", "POST", null, "FORBIDDEN", "Access denied: Non-admin user attempted admin login");
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\": \"Access denied: Administrator privileges required\"}");
	        }

            // SOC 2 Control 4: Multi-Factor Authentication Check
            if (user.isMfaEnabled()) {
                String mfaTempToken = jwtUtil.generateMfaTempToken(username, role, "STAFF", user.getId());
                auditLogService.logAction(user.getUsername(), role, "MFA_CHALLENGE_ISSUED", "AUTH", "/api/v2/adminlogin", "POST", null, "PENDING", "MFA required for admin user: " + username);
                Map<String, Object> mfaResponse = new HashMap<>();
                mfaResponse.put("mfaRequired", true);
                mfaResponse.put("tempToken", mfaTempToken);
                mfaResponse.put("userId", user.getId());
                mfaResponse.put("userType", "STAFF");
                mfaResponse.put("message", "Multi-factor authentication code required");
                return ResponseEntity.ok(mfaResponse);
            }

	        String jwtToken = jwtUtil.generateToken(username, role); // Use jwtUtil
>>>>>>> internship/main
	        Map<String, Object> responseBody = new HashMap<>();
	        responseBody.put("Token", jwtToken);
	        responseBody.put("message", "Login successful");
	        responseBody.put("UserName", user.getUsername());
	        responseBody.put("Email", user.getEmail());
	        responseBody.put("AdminId", user.getId());
	        responseBody.put("Role", role); // Add role to the response body

<<<<<<< HEAD
	        // Logging user information
	        System.out.println("Username: " + user.getUsername());
	        System.out.println("JWT Token: " + jwtToken);
	        System.out.println("User ID: " + user.getId());
	        System.out.println("Email: " + user.getEmail());
=======
	        // SOC 2 Audit Trail: Record admin login success event
	        auditLogService.logAction(user.getUsername(), role, "ADMIN_LOGIN", "AUTH", "/api/v2/adminlogin", "POST", null, "SUCCESS", "Admin user logged in successfully");
>>>>>>> internship/main

	        // Successful login
	        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
	    } catch (Exception e) {
	        // Log the exception for further investigation if needed
	        e.printStackTrace();
	        logger.error("", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during login");
	    }
	}
	
    public ResponseEntity<String> logoutadmin(@RequestHeader("Authorization") String token) {
<<<<<<< HEAD
        // Extract the token from the Authorization header
        // Check if the token is valid (e.g., not expired)
        // Blacklist the token to invalidate it
        tokenBlacklist.blacklistToken(token);
        System.out.println("Logged out successfully");
        // Respond with a success message
=======
        tokenBlacklist.blacklistToken(token);
        System.out.println("Logged out successfully");

        // ISO27001 Audit Trail: Record admin logout event
        auditLogService.logAction("ADMIN", "ADMIN", "LOGOUT", "AUTH", "/api/v2/adminlogout", "POST", null, "SUCCESS", "Admin user logged out successfully");

>>>>>>> internship/main
        return ResponseEntity.ok().body("Logged out successfully");
    }


	public boolean getall() {
	    // Retrieve all licenses
	    Iterable<License> licenseIterable = licenseRepository.findAll();
	    List<License> licenseList = StreamSupport.stream(licenseIterable.spliterator(), false)
	                                             .collect(Collectors.toList());
	    
	    // Get current date
	    LocalDate currentDate = LocalDate.now();
	    java.util.Date Datecurrent = java.sql.Date.valueOf(currentDate);
	    long milliseconds = Datecurrent.getTime(); // Get the time in milliseconds
	    java.sql.Timestamp timestamp = new java.sql.Timestamp(milliseconds);
	    
	    // Iterate over licenses
	    for (License license : licenseList) {
	        System.out.println("---------------------------------------------------");
	        System.out.println("ID: " + license.getId());
	        System.out.println("Company Name: " + license.getCompany_name());
	        System.out.println("Product Name: " + license.getProduct_name());
	        System.out.println("Key: " + license.getKey());
	        System.out.println("Start Date: " + license.getStart_date());
	        System.out.println("End Date: " + license.getEnd_date());
	        System.out.println("Is End Date Equal to Current Date: " + license.getEnd_date().equals(timestamp));
	        // Print other fields as needed
	    }
	    
	    boolean valid = false; // Initialize valid to false
	    
	    System.out.println("out of the loop" + valid);
	    
	    // Check license validity
	    for (License license : licenseList) {
	        // Check the validity condition
	        if (license.getEnd_date().equals(timestamp)) {
	            valid = false; // Set valid to false if at least one license is valid
	            System.out.println("inside of the loop" + valid);
	            break; // No need to continue checking, we already found a valid license
	        } else {
	            valid = true;
	            System.out.println("inside of the loop" + valid);
	        }
	    }
	    
	    return valid;
	}

	 
	 

	 public ResponseEntity<UserListWithStatus> getUser(@PathVariable Long userId) {
	     // Assuming adduserrepository is your repository for AddUser entity
	     Optional<AddUser> userOptional = adduserrepository.findById(userId);
	     if (userOptional.isPresent()) {
	         AddUser user = userOptional.get();
<<<<<<< HEAD
	         // Retrieve the user's data
	         
	         // Now you can construct the response as needed
	         // For example:
=======
	         // Sanitize sensitive credentials before returning
	         user.setPassword(null);
	         user.setConfirmPassword(null);
>>>>>>> internship/main
	         return new ResponseEntity<>(new UserListWithStatus(Collections.singletonList(user), false, true), HttpStatus.OK);
	     } else {
	         // User not found with the given ID
	         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	     }
	 }


	
	
	

	 public ResponseEntity<String> deleteUser(
		        @RequestHeader("Authorization") String token, 
		        @PathVariable Long UserId
		) {
		    try {
		        // Extract role from the token
		        String role = jwtUtil.getRoleFromToken(token);
		        System.out.println("role: " + role);

		        // Check if the role is "ADMIN"
		        if (!"ADMIN".equals(role)) {
		            // Return a Forbidden response if the role is not "ADMIN"
		            return ResponseEntity.status(HttpStatus.FORBIDDEN)
		                                 .body("{\"message\": \"Only admin can delete subadmin\"}");
		        }

		        // Perform the delete operation if the role is "ADMIN"
		        adduserrepository.deleteById(UserId);

<<<<<<< HEAD
		        // Return a success message with 204 status
		        return ResponseEntity.status(HttpStatus.NO_CONTENT)
		                             .body("{\"message\": \"Admin deleted successfully\"}");
=======
		        // ISO 27001 Audit Trail
		        auditLogService.logAction("ADMIN", "ADMIN", "ADMIN_USER_DELETED", "ADMIN", "/api/v2/DeleteUser/" + UserId, "DELETE", null, "SUCCESS", "Administrator deleted user with ID: " + UserId);

		        // Return a success message
		        return ResponseEntity.ok()
		                             .body("{\"message\": \"User deleted successfully\"}");
>>>>>>> internship/main
		    } catch (Exception e) {
		        // Return an internal server error in case of any exception
		    	logger.error("", e);
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
<<<<<<< HEAD
		                             .body("{\"message\": \"An error occurred while deleting the admin.\"}");
=======
		                             .body("{\"message\": \"An error occurred while deleting the user: " + e.getMessage() + "\"}");
>>>>>>> internship/main
		    }
		}
	 
	 
	    public ResponseEntity<String> deleteMultipleAdmins(
	            @RequestHeader("Authorization") String token, 
	            @RequestBody List<Long> userIds // Accept a list of user IDs
	    ) {
	        try {
	            // Extract role from the token
	            String role = jwtUtil.getRoleFromToken(token);
	            System.out.println("role: " + role);

	            // Check if the role is "ADMIN"
	            if (!"ADMIN".equals(role)) {
	                // Return a Forbidden response if the role is not "ADMIN"
	                return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                        .body("{\"message\": \"Only admin can delete subadmins\"}");
	            }

	            // Perform the delete operation for each user ID
	            for (Long userId : userIds) {
	                if (adduserrepository.existsById(userId)) {
	                    adduserrepository.deleteById(userId);
	                } else {
	                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                            .body("{\"message\": \"Admin with ID " + userId + " not found\"}");
	                }
	            }

	            // Return a success message
<<<<<<< HEAD
	            return ResponseEntity.status(HttpStatus.NO_CONTENT)
=======
	            return ResponseEntity.ok()
>>>>>>> internship/main
	                    .body("{\"message\": \"Admins deleted successfully\"}");

	        } catch (Exception e) {
	            // Return an internal server error in case of any exception
	        	logger.error("", e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
<<<<<<< HEAD
	                    .body("{\"message\": \"An error occurred while deleting admins.\"}");
=======
	                    .body("{\"message\": \"An error occurred while deleting admins: " + e.getMessage() + "\"}");
>>>>>>> internship/main
	        }
	    }


	

<<<<<<< HEAD
	public ResponseEntity<String> updateUserDetails(@PathVariable Long userId, @RequestBody AddUser updatedUserData) {
=======
	public ResponseEntity<String> updateUserDetails(Long userId, AddUser updatedUserData, String token) {
>>>>>>> internship/main
	    try {
	        // Retrieve existing user data from the repository
	        AddUser existingUser = adduserrepository.findById(userId)
	                .orElseThrow(() -> new RuntimeException("User not found"));

<<<<<<< HEAD
	        // Apply partial updates to the existing user data
	        if (updatedUserData.getUsername() != null) {
	            existingUser.setUsername(updatedUserData.getUsername());
	        }

	        // Update additional fields
	        if (updatedUserData.getEmail() != null) {
	            existingUser.setEmail(updatedUserData.getEmail());
	        }

	        
=======
	        // SOC 2 Control 5 & 7: Prevent privilege escalation
	        if (updatedUserData.getRole() != null && !updatedUserData.getRole().trim().isEmpty() && !updatedUserData.getRole().equalsIgnoreCase(existingUser.getRole())) {
	            if (token == null || !jwtUtil.validateToken(token)) {
	                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\": \"Only authorized administrators can modify user roles\"}");
	            }
	            String callerRole = jwtUtil.getRoleFromToken(token);
	            if (!"ADMIN".equalsIgnoreCase(callerRole)) {
	                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\": \"Only authorized administrators can modify user roles\"}");
	            }
	            existingUser.setRole(updatedUserData.getRole().trim().toUpperCase());
	        }

	        // Apply partial updates to the existing user data
	        if (updatedUserData.getUsername() != null && !updatedUserData.getUsername().trim().isEmpty()) {
	            existingUser.setUsername(updatedUserData.getUsername().trim());
	        }

	        if (updatedUserData.getEmail() != null && !updatedUserData.getEmail().trim().isEmpty()) {
	            existingUser.setEmail(updatedUserData.getEmail().trim());
	        }

>>>>>>> internship/main
	        if (updatedUserData.getMobnum() != null) {
	            existingUser.setMobnum(updatedUserData.getMobnum());
	        }

	        if (updatedUserData.getCompname() != null) {
	            existingUser.setCompname(updatedUserData.getCompname());
	        }

	        if (updatedUserData.getPincode() != null) {
	            existingUser.setPincode(updatedUserData.getPincode());
	        }

	        if (updatedUserData.getCountry() != null) {
	            existingUser.setCountry(updatedUserData.getCountry());
	        }

<<<<<<< HEAD
	        if (updatedUserData.getPassword() != null) {
	            existingUser.setPassword(updatedUserData.getPassword());
	        }

	        if (updatedUserData.getConfirmPassword() != null) {
	            existingUser.setConfirmPassword(updatedUserData.getConfirmPassword());
=======
	        if (updatedUserData.getPassword() != null && !updatedUserData.getPassword().trim().isEmpty()) {
	            String rawPassword = updatedUserData.getPassword().trim();
	            com.VsmartEngine.MediaJungle.security.PasswordPolicyService.ValidationResult updatePolicy = passwordPolicyService.validate(rawPassword);
	            if (!updatePolicy.isValid()) {
	                return ResponseEntity.badRequest().body("{\"message\": \"" + updatePolicy.getErrorMessage() + "\"}");
	            }
	            String hashedPassword = PasswordSecurityUtil.encodeIfRaw(passwordEncoder, rawPassword);
	            existingUser.setPassword(hashedPassword);
	            existingUser.setConfirmPassword(null);
>>>>>>> internship/main
	        }

	        if (updatedUserData.getAddress() != null) {
	            existingUser.setAddress(updatedUserData.getAddress());
	        }

	        // Save the updated user data back to the repository
	        adduserrepository.save(existingUser);

<<<<<<< HEAD
	        return new ResponseEntity<>("User details updated successfully", HttpStatus.OK);
	    } catch (Exception e) {
	    	logger.error("", e);
	        return new ResponseEntity<>("Error updating user details", HttpStatus.INTERNAL_SERVER_ERROR);
=======
	        // ISO 27001 Audit Trail
	        auditLogService.logAction(existingUser.getUsername(), existingUser.getRole(), "ADMIN_USER_UPDATED", "ADMIN", "/api/v2/UpdateUser/" + userId, "PATCH", null, "SUCCESS", "Administrator updated user: " + existingUser.getUsername());

	        return ResponseEntity.ok("{\"message\": \"User details updated successfully\"}");
	    } catch (Exception e) {
	    	logger.error("", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Error updating user details: " + e.getMessage() + "\"}");
>>>>>>> internship/main
	    }
	}

	public ResponseEntity<?> checkAdminRole() {
	    try {
	        Optional<AddUser> adminOptional = adduserrepository.findByRole("ADMIN");
<<<<<<< HEAD
	        return ResponseEntity.ok(Collections.singletonMap("adminExists", adminOptional.isPresent()));
=======
	        if (adminOptional.isPresent()) {
	            AddUser admin = adminOptional.get();
	            Map<String, Object> res = new HashMap<>();
	            res.put("adminExists", true);
	            res.put("username", admin.getUsername());
	            res.put("email", admin.getEmail());
	            return ResponseEntity.ok(res);
	        } else {
	            return ResponseEntity.ok(Collections.singletonMap("adminExists", false));
	        }
>>>>>>> internship/main
	    } catch (Exception e) {
	    	logger.error("", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to check admin role");
	    }
	}

<<<<<<< HEAD
=======
	public ResponseEntity<?> resetAdminPassword(String username, String newPassword, String token) {
	    try {
	        Optional<AddUser> userOptional = adduserrepository.findByUsername(username);
	        if (userOptional.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Admin user not found."));
	        }
	        AddUser admin = userOptional.get();

            // SOC 2 Control 7: Privileged Access Authorization Check
            boolean isAuthorized = false;
            if (token != null && jwtUtil.validateToken(token)) {
                String tokenRole = jwtUtil.getRoleFromToken(token);
                String tokenUser = jwtUtil.getUsernameFromToken(token);
                if ("ADMIN".equalsIgnoreCase(tokenRole) || username.equalsIgnoreCase(tokenUser)) {
                    isAuthorized = true;
                }
            } else if (admin.getEmail() != null && verificationController.isEmailVerified(admin.getEmail())) {
                isAuthorized = true;
                verificationController.consumeEmailVerification(admin.getEmail());
            }

            if (!isAuthorized) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Admin password reset requires authenticated administrator or verified email OTP."));
            }

	        com.VsmartEngine.MediaJungle.security.PasswordPolicyService.ValidationResult resetPolicy = passwordPolicyService.validate(newPassword);
	        if (!resetPolicy.isValid()) {
	            return ResponseEntity.badRequest().body(Map.of("message", resetPolicy.getErrorMessage()));
	        }
	        String encoded = PasswordSecurityUtil.encodeIfRaw(passwordEncoder, newPassword);
	        admin.setPassword(encoded);
	        admin.setConfirmPassword(null);
	        adduserrepository.save(admin);

            auditLogService.logAction(admin.getUsername(), admin.getRole(), "PASSWORD_RESET", "ADMIN", "/api/v2/resetAdminPassword", "POST", null, "SUCCESS", "Admin password reset successfully for user: " + username);

	        return ResponseEntity.ok(Map.of("message", "Admin password updated successfully."));
	    } catch (Exception e) {
	        logger.error("", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to reset admin password."));
	    }
	}

    // =========================================================================
    // SOC 2 Control 3: ADMIN UNLOCK STAFF ACCOUNT
    // =========================================================================
    public ResponseEntity<?> adminUnlockStaff(
            Long id,
            String token) {
        try {
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or missing token"));
            }
            String role = jwtUtil.getRoleFromToken(token);
            if (!"ADMIN".equalsIgnoreCase(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Only administrator can unlock staff"));
            }

            Optional<AddUser> userOptional = adduserrepository.findById(id);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Staff not found with ID: " + id));
            }

            AddUser user = userOptional.get();
            user.setAccountLocked(false);
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(null);
            adduserrepository.save(user);

            String adminUser = jwtUtil.getUsernameFromToken(token);
            auditLogService.logAction(adminUser != null ? adminUser : "ADMIN", role, "ACCOUNT_UNLOCKED_BY_ADMIN", "ADMIN", "/api/v2/admin/staff/" + id + "/unlock", "POST", null, "SUCCESS", "Administrator unlocked staff account: " + user.getUsername());

            return ResponseEntity.ok(Map.of("message", "Staff account unlocked successfully", "staffId", id));
        } catch (Exception e) {
            logger.error("Error in adminUnlockStaff", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error unlocking staff: " + e.getMessage()));
        }
    }

    // =========================================================================
    // SOC 2 Control 6 & 7: ADMIN UPDATE STAFF ACCOUNT STATUS
    // =========================================================================
    public ResponseEntity<?> adminUpdateStaffStatus(
            Long id,
            Map<String, String> statusUpdate,
            String token) {
        try {
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or missing token"));
            }
            String role = jwtUtil.getRoleFromToken(token);
            if (!"ADMIN".equalsIgnoreCase(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Only administrator can update staff status"));
            }

            Optional<AddUser> userOptional = adduserrepository.findById(id);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Staff not found with ID: " + id));
            }

            String newStatus = statusUpdate != null ? statusUpdate.get("status") : null;
            if (newStatus == null || (!newStatus.equalsIgnoreCase("ACTIVE") &&
                    !newStatus.equalsIgnoreCase("SUSPENDED") &&
                    !newStatus.equalsIgnoreCase("DEACTIVATED") &&
                    !newStatus.equalsIgnoreCase("LOCKED"))) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid status value. Allowed: ACTIVE, SUSPENDED, DEACTIVATED, LOCKED"));
            }

            AddUser user = userOptional.get();
            String oldStatus = user.getStatus();
            user.setStatus(newStatus.toUpperCase());
            if ("ACTIVE".equalsIgnoreCase(newStatus)) {
                user.setAccountLocked(false);
                user.setFailedLoginAttempts(0);
                user.setAccountLockedUntil(null);
            } else if ("LOCKED".equalsIgnoreCase(newStatus)) {
                user.setAccountLocked(true);
            }
            adduserrepository.save(user);

            String adminUser = jwtUtil.getUsernameFromToken(token);
            auditLogService.logAction(adminUser != null ? adminUser : "ADMIN", role, "STAFF_STATUS_UPDATED", "ADMIN", "/api/v2/admin/staff/" + id + "/status", "PATCH", null, "SUCCESS", "Administrator changed staff " + user.getUsername() + " status from " + oldStatus + " to " + newStatus.toUpperCase());

            return ResponseEntity.ok(Map.of(
                    "message", "Staff status updated successfully",
                    "staffId", id,
                    "oldStatus", oldStatus != null ? oldStatus : "ACTIVE",
                    "newStatus", newStatus.toUpperCase()
            ));
        } catch (Exception e) {
            logger.error("Error in adminUpdateStaffStatus", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error updating staff status: " + e.getMessage()));
        }
    }

>>>>>>> internship/main
}
