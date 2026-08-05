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
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import com.VsmartEngine.MediaJungle.accessmanagement.service.AccessManagementService;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin()
@RestController
@RequestMapping("/api/v2/")
public class UserRegisterController {
    
    @Autowired
    private UserRegisterRepository userregisterrepository;
    
    @Autowired
    private JwtUtil jwtUtil; // Autowire JwtUtil

    @Autowired
    private TokenBlacklist tokenBlacklist;
    
	@Autowired
    private NotificationService notificationservice;  

	@Autowired
	private AccessManagementService accessManagementService;
	
	private static final Logger logger = LoggerFactory.getLogger(UserRegisterController.class);
       
	   public ResponseEntity<?> register(
		        @RequestParam("username") String username,
		        @RequestParam("email") String email,
		        @RequestParam("password") String password,
		        @RequestParam("mobnum") String mobnum,
		        @RequestParam(value = "profile", required = false) MultipartFile profile) {
		    try {
		        // Encrypt the password and confirmPassword
		        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		        String encodedPassword = passwordEncoder.encode(password);
		        // Set current date
		        LocalDate parsedDate = LocalDate.now();

		        // Create a new UserRegister object
		        UserRegister newRegister = new UserRegister();
		        newRegister.setUsername(username);
		        newRegister.setEmail(email);
		        newRegister.setPassword(encodedPassword);
		        newRegister.setMobnum(mobnum);
		        newRegister.setDate(parsedDate);

		        // Handle profile image
		        if (profile != null && !profile.isEmpty()) {
		            byte[] thumbnailBytes = ImageUtils.compressImage(profile.getBytes());
		            newRegister.setProfile(thumbnailBytes);
		        }

		        // Save the user to the repository
		        UserRegister savedUser = userregisterrepository.save(newRegister);

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
            
            // Check if the list is empty
            if (getUser.isEmpty()) {
                return new ResponseEntity(HttpStatus.NO_CONTENT); // Return 204 No Content
            }
            
            // Return the list of users with 200 OK
            return new ResponseEntity<>(getUser, HttpStatus.OK);
        } catch (Exception e) {
            // Handle exceptions and return 500 Internal Server Error
        	logger.error("", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<UserRegister> getUserById(@PathVariable Long id) {
        Optional<UserRegister> userOptional = userregisterrepository.findById(id);
        if (userOptional.isPresent()) {
            UserRegister user = userOptional.get();
            // Check if the profile is present (not null and not empty)
            if (user.getProfile() != null && user.getProfile().length > 0) {
                byte[] images = ImageUtils.decompressImage(user.getProfile());
                user.setProfile(images);
            }
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
  
    // ISO 27001 | Module 1: Access Management | Task 1, 3, 5, 7, 9
    // Description: Authenticates user, checks deprovisioned status, validates MFA if enabled, logs login activity, and registers active session.
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest, HttpServletRequest request) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        String mfaCode = loginRequest.get("mfaCode");
        String remoteAddr = request != null ? request.getRemoteAddr() : "127.0.0.1";
        String userAgent = request != null ? request.getHeader("User-Agent") : "Unknown";

        Optional<UserRegister> userOptional = userregisterrepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            accessManagementService.recordLoginActivity(email, "FAILED_USER_NOT_FOUND", remoteAddr, userAgent, "User email not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"User not found\"}");
        }
        UserRegister user = userOptional.get();

        // ISO 27001 Task 7: User Deprovisioning Check
        if ("DEPROVISIONED".equalsIgnoreCase(user.getStatus()) || Boolean.FALSE.equals(user.getAccountNonLocked())) {
            accessManagementService.recordLoginActivity(email, "FAILED_DEPROVISIONED", remoteAddr, userAgent, "Account is deprovisioned or locked");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\": \"Account is deprovisioned or locked. Access denied.\"}");
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            accessManagementService.recordLoginActivity(email, "FAILED_INVALID_PASSWORD", remoteAddr, userAgent, "Invalid password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Incorrect password\"}");
        }

        // ISO 27001 Task 3: MFA Check
        if (Boolean.TRUE.equals(user.getMfaEnabled())) {
            if (mfaCode == null || !accessManagementService.validateMfaCode(email, mfaCode)) {
                accessManagementService.recordLoginActivity(email, "FAILED_MFA", remoteAddr, userAgent, "Invalid or missing MFA OTP code");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "MFA verification required", "mfaRequired", true));
            }
        }

        // ISO 27001 Task 1: Role-Based Access Token
        String role = user.getRole() != null ? user.getRole() : "USER";
        String jwtToken = jwtUtil.generateToken(email, role);

        user.setLastLoginAt(LocalDateTime.now());
        userregisterrepository.save(user);

        // ISO 27001 Task 5: Active Session Registration
        accessManagementService.createSession(email, jwtToken, remoteAddr, userAgent);

        // ISO 27001 Task 9: Record Successful Login Activity
        accessManagementService.recordLoginActivity(email, "SUCCESS", remoteAddr, userAgent, null);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", jwtToken);
        responseBody.put("message", "Login successful");
        responseBody.put("name", user.getUsername());
        responseBody.put("email", user.getEmail());
        responseBody.put("userId", user.getId());
        responseBody.put("role", role);
        responseBody.put("mfaEnabled", user.getMfaEnabled());

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        // Extract the token from the Authorization header
        // Check if the token is valid (e.g., not expired)
        // Blacklist the token to invalidate it
        tokenBlacklist.blacklistToken(token);
        System.out.println("Logged out successfully");
        // Respond with a success message
        return ResponseEntity.ok().body("Logged out successfully");
    }
    
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> loginRequest) {
        try {
            // Finding the user by email
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");
            Optional<UserRegister> userOptional = userregisterrepository.findByEmail(email);

            // If the user doesn't exist, return 404 Not Found
            if (!userOptional.isPresent()) {
                System.out.println("User not found: " + email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            UserRegister user = userOptional.get();

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(password);

            // Update the user password
            user.setPassword(encodedPassword);

            // Save the updated user
            UserRegister savedUser = userregisterrepository.save(user);
            Long userId = savedUser.getId();
            String name = savedUser.getUsername();
            String heading = name + " successfully changed your password";

            // Create notification and associate with user
            Long notifyId = notificationservice.createNotification(name, email, heading);
            
            if (notifyId != null) {
                notificationservice.notificationuser(notifyId, userId);
            }

            return ResponseEntity.ok("Password reset successfully");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
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
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (password != null) {
            	String encodedPassword = passwordEncoder.encode(password);
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

   

}