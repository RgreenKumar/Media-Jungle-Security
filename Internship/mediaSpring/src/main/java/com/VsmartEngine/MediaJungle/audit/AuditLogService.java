// ===========================================
// Internship Security Enhancement
// Feature : Audit Logging
// ISO27001 Control : Logging & Monitoring
// ===========================================
package com.VsmartEngine.MediaJungle.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

// ISO27001 Audit Trail: Asynchronous Audit Logging Service
@Service
public class AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);
    private final AuditLogRepository auditLogRepository;

    // Constructor Injection
    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // ISO27001 Audit Trail: Reusable asynchronous log saving method
    @Async
    public void logAction(String username, String role, String action, String module,
                          String requestUrl, String httpMethod, String ipAddress,
                          String status, String description) {
        try {
            HttpServletRequest request = getCurrentHttpRequest();

            String finalUrl = (requestUrl != null && !requestUrl.isEmpty()) ? requestUrl :
                    (request != null ? request.getRequestURI() : "/api");

            String finalMethod = (httpMethod != null && !httpMethod.isEmpty()) ? httpMethod :
                    (request != null ? request.getMethod() : "SYSTEM");

            String finalIp = (ipAddress != null && !ipAddress.isEmpty()) ? ipAddress :
                    (request != null ? getClientIpAddress(request) : "127.0.0.1");

            // ISO27001 Audit Trail: Construct audit entry
            AuditLog auditLog = new AuditLog(
                    username != null ? username : "ANONYMOUS",
                    role != null ? role : "NONE",
                    action,
                    module,
                    finalUrl,
                    finalMethod,
                    finalIp,
                    status,
                    description
            );

            // Save log to PostgreSQL database
            auditLogRepository.save(auditLog);
            logger.info("ISO27001 Audit Log Saved | Action: {} | User: {} | Status: {}", action, username, status);
        } catch (Exception e) {
            // Never interrupt application execution if audit logging encounters an exception
            logger.error("Audit log failed to save gracefully: {}", e.getMessage());
        }
    }

    // Helper method: Get current HTTP Servlet Request from thread context
    public HttpServletRequest getCurrentHttpRequest() {
        try {
            RequestAttributes reqAttr = RequestContextHolder.getRequestAttributes();
            if (reqAttr instanceof ServletRequestAttributes) {
                return ((ServletRequestAttributes) reqAttr).getRequest();
            }
        } catch (Exception e) {
            // Ignore if called outside HTTP request context
        }
        return null;
    }

    // Helper method: Extract client IP Address from HTTP Servlet Request
    public String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "127.0.0.1";
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
