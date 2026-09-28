// =======================================
// Internship Security Enhancement
// Feature: Role Based Access Control
// ISO27001 Control: Access Control
// =======================================
package com.VsmartEngine.MediaJungle.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// RBAC: Custom Access Denied Handler for Authorization Failures (HTTP 403)
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
    private final com.VsmartEngine.MediaJungle.audit.AuditLogService auditLogService;

    // Constructor Injection
    public CustomAccessDeniedHandler(com.VsmartEngine.MediaJungle.audit.AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        // RBAC: Retrieve current user authentication context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null) ? auth.getName() : "Anonymous";
        String role = (auth != null && !auth.getAuthorities().isEmpty()) 
                      ? auth.getAuthorities().iterator().next().getAuthority() 
                      : "NONE";
        String requestedUrl = request.getRequestURI();
        LocalDateTime timestamp = LocalDateTime.now();

        // ISO27001 Audit Log: Log unauthorized API access attempt
        logger.warn("UNAUTHORIZED ACCESS ATTEMPT | Username: {} | Role: {} | Requested URL: {} | Timestamp: {}",
                username, role, requestedUrl, timestamp);

        // ISO27001 Audit Trail: Store forbidden access attempt in PostgreSQL audit_log
        auditLogService.logAction(username, role, "UNAUTHORIZED_ACCESS", "SECURITY", requestedUrl, request.getMethod(), request.getRemoteAddr(), "FORBIDDEN", "Access Denied to URL: " + requestedUrl);

        // RBAC: Return HTTP 403 status with JSON body as required
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = String.format("{\"status\":%d,\"message\":\"Access Denied\"}", HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(jsonResponse);
    }
}
