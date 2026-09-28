package com.VsmartEngine.MediaJungle;
<<<<<<< HEAD
=======

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

>>>>>>> internship/main
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

=======
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

// =============================================================================
// SOC 2 Control 17: Secure Error Handling
// Enforce structured, sanitized error responses without leaking stack traces,
// database errors, file paths, credentials, or internal implementation details.
// =============================================================================
>>>>>>> internship/main
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
<<<<<<< HEAD

    // Catch any generic Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {
        // Log the error
   	 ex.printStackTrace();
   	log.error("", ex);
        // Return a generic error response
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
=======
    private final com.VsmartEngine.MediaJungle.audit.AuditLogService auditLogService;

    public GlobalExceptionHandler(com.VsmartEngine.MediaJungle.audit.AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    // RBAC / Authorization Failure (HTTP 403)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        
        log.warn("RBAC Authorization Failure at {}: {}", request.getDescription(false), ex.getMessage());
        auditLogService.logAction(null, "NONE", "UNAUTHORIZED_ACCESS", "SECURITY",
                request.getDescription(false), "SYSTEM", null, "FORBIDDEN", "Access Denied: " + ex.getMessage());

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", Instant.now().toString());
        error.put("status", HttpStatus.FORBIDDEN.value());
        error.put("error", "Forbidden");
        error.put("message", "Access denied: You do not have permission to access this resource.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // Authentication Failure (HTTP 401)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {

        log.warn("Authentication failure at {}: {}", request.getDescription(false), ex.getMessage());
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", Instant.now().toString());
        error.put("status", HttpStatus.UNAUTHORIZED.value());
        error.put("error", "Unauthorized");
        error.put("message", "Invalid or missing authentication credentials.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // Input Validation Failure (HTTP 400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.warn("Input validation failure at {}: {}", request.getDescription(false), fieldErrors);
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", Instant.now().toString());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", "Input validation failed");
        error.put("fieldErrors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // File Upload Exceeds Limit (HTTP 413)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxSizeException(
            MaxUploadSizeExceededException ex, WebRequest request) {

        log.warn("Payload too large at {}: {}", request.getDescription(false), ex.getMessage());
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", Instant.now().toString());
        error.put("status", HttpStatus.PAYLOAD_TOO_LARGE.value());
        error.put("error", "Payload Too Large");
        error.put("message", "Uploaded file size exceeds maximum configured limit.");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
    }

    // Catch-All Generic Exception (HTTP 500)
    // Never expose stack trace, database vendor, or query details to clients
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unhandled internal server error at {}: ", request.getDescription(false), ex);

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", Instant.now().toString());
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred. Please contact support if the issue persists.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
>>>>>>> internship/main
    }
}

