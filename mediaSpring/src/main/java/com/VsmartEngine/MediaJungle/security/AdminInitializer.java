// =======================================
// Internship Security Enhancement
// Feature: Role Based Access Control
// ISO27001 Control: Access Control
// =======================================
package com.VsmartEngine.MediaJungle.security;

import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.VsmartEngine.MediaJungle.userregister.UserRegister;
import com.VsmartEngine.MediaJungle.userregister.UserRegisterRepository;

// RBAC: Safe Admin Initialization on Application Startup
@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);
    private final UserRegisterRepository userRegisterRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    // Constructor Injection
    public AdminInitializer(UserRegisterRepository userRegisterRepository, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.userRegisterRepository = userRegisterRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Safe Admin Creation: Check if default admin account exists
        Optional<UserRegister> adminOpt = userRegisterRepository.findByEmail("abhishek@gmail.com");

        if (adminOpt.isPresent()) {
            UserRegister adminUser = adminOpt.get();
            // Ensure existing admin user has ADMIN role
            if (!UserRole.ADMIN.name().equalsIgnoreCase(adminUser.getRole())) {
                adminUser.setRole(UserRole.ADMIN.name());
                userRegisterRepository.save(adminUser);
                logger.info("RBAC: Updated user 'abhishek@gmail.com' role to ADMIN.");
            }
        } else {
            // Create default admin safely
            UserRegister newAdmin = new UserRegister();
            newAdmin.setUsername("Abhishek");
            newAdmin.setEmail("abhishek@gmail.com");
            newAdmin.setPassword(passwordEncoder.encode("Admin@123"));
            newAdmin.setDate(LocalDate.now());
            newAdmin.setRole(UserRole.ADMIN.name()); // Only Admin Allowed
            userRegisterRepository.save(newAdmin);
            logger.info("RBAC: Default Admin account created safely with email 'abhishek@gmail.com'.");
        }
    }
}
