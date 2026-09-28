// ===========================================
// Internship Security Enhancement
// Feature : Secure File Upload Validation
// ISO27001 Control : Secure File Handling
// ===========================================
package com.VsmartEngine.MediaJungle.upload;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.VsmartEngine.MediaJungle.audit.AuditLogService;

// ISO27001 Secure File Handling: Reusable File Validation Service
@Service
public class FileValidationService {

    private static final Logger logger = LoggerFactory.getLogger(FileValidationService.class);

    private final FileMetadataRepository fileMetadataRepository;
    private final AuditLogService auditLogService;

    @Value("${security.upload.max-image-size:10485760}")
    private long maxImageSize;

    @Value("${security.upload.max-audio-size:52428800}")
    private long maxAudioSize;

    @Value("${security.upload.max-video-size:524288000}")
    private long maxVideoSize;

    // Block dangerous executable extensions immediately
    private static final Set<String> DANGEROUS_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".exe", ".bat", ".cmd", ".sh", ".jsp", ".php", ".js", ".jar", ".com", ".scr", ".vbs"
    ));

    // Whitelisted MIME Types
    private static final Set<String> ALLOWED_IMAGE_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/png", "image/webp"
    ));

    private static final Set<String> ALLOWED_VIDEO_TYPES = new HashSet<>(Arrays.asList(
            "video/mp4", "video/x-matroska", "video/webm"
    ));

    private static final Set<String> ALLOWED_AUDIO_TYPES = new HashSet<>(Arrays.asList(
            "audio/mpeg", "audio/wav", "audio/aac", "audio/mp3", "audio/x-wav"
    ));

    // Constructor Injection
    public FileValidationService(FileMetadataRepository fileMetadataRepository, AuditLogService auditLogService) {
        this.fileMetadataRepository = fileMetadataRepository;
        this.auditLogService = auditLogService;
    }

    // ISO27001 Secure File Handling: Reusable File Validation Method
    public ResponseEntity<?> validateFile(MultipartFile file, String category, String username, String role) {
        if (file == null || file.isEmpty()) {
            return null; // Empty optional file is allowed
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "file";
        }

        // Prevent Path Traversal
        String sanitizedFilename = sanitizeFilename(originalFilename);
        String lowerFilename = sanitizedFilename.toLowerCase();

        // Block dangerous executable files immediately
        for (String ext : DANGEROUS_EXTENSIONS) {
            if (lowerFilename.endsWith(ext)) {
                // Log upload attempt
                if (auditLogService != null) {
                    auditLogService.logAction(username, role, "BLOCKED_FILE_UPLOAD", "FILE_SECURITY", "/upload", "POST", null, "REJECTED", "Executable file upload blocked: " + sanitizedFilename);
                }
                logger.warn("Block executable files: Rejected dangerous file {}", sanitizedFilename);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(400, "Executable files are not allowed."));
            }
        }

        // Validate MIME type
        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        long fileSize = file.getSize();
        long maxAllowedSize = maxImageSize;

        if ("IMAGE".equalsIgnoreCase(category)) {
            if (!ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
                // Log upload attempt
                if (auditLogService != null) {
                    auditLogService.logAction(username, role, "INVALID_MIME_TYPE", "FILE_SECURITY", "/upload", "POST", null, "REJECTED", "Invalid image MIME type: " + contentType);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(400, "Invalid file type. Allowed image types: JPEG, PNG, WEBP."));
            }
            maxAllowedSize = maxImageSize;
        } else if ("VIDEO".equalsIgnoreCase(category)) {
            if (!ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase())) {
                // Log upload attempt
                if (auditLogService != null) {
                    auditLogService.logAction(username, role, "INVALID_MIME_TYPE", "FILE_SECURITY", "/upload", "POST", null, "REJECTED", "Invalid video MIME type: " + contentType);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(400, "Invalid file type. Allowed video types: MP4, MKV, WEBM."));
            }
            maxAllowedSize = maxVideoSize;
        } else if ("AUDIO".equalsIgnoreCase(category)) {
            if (!ALLOWED_AUDIO_TYPES.contains(contentType.toLowerCase())) {
                // Log upload attempt
                if (auditLogService != null) {
                    auditLogService.logAction(username, role, "INVALID_MIME_TYPE", "FILE_SECURITY", "/upload", "POST", null, "REJECTED", "Invalid audio MIME type: " + contentType);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(400, "Invalid file type. Allowed audio types: MP3, WAV, AAC."));
            }
            maxAllowedSize = maxAudioSize;
        }

        // Check Maximum Upload Size
        if (fileSize > maxAllowedSize) {
            // Log upload attempt
            if (auditLogService != null) {
                auditLogService.logAction(username, role, "LARGE_FILE_UPLOAD", "FILE_SECURITY", "/upload", "POST", null, "REJECTED", "File size (" + fileSize + " bytes) exceeds maximum limit (" + maxAllowedSize + " bytes)");
            }
            logger.warn("File size limit exceeded: {} bytes > {} bytes", fileSize, maxAllowedSize);
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(createErrorResponse(413, "File exceeds maximum allowed size."));
        }

        return null; // File passes all security validation checks
    }

    // Generate UUID filename preserving original extension
    public String generateSecureFilename(String originalFilename) {
        String sanitized = sanitizeFilename(originalFilename);
        String extension = "";
        int dotIndex = sanitized.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = sanitized.substring(dotIndex);
        }
        // Generate UUID filename
        return UUID.randomUUID().toString() + extension;
    }

    // Prevent Path Traversal by sanitizing dangerous characters
    public String sanitizeFilename(String filename) {
        if (filename == null) return "file";
        // Remove ../, \, :, %, *, ?, <, >, |
        return filename.replaceAll("\\.\\.[\\\\/]", "")
                .replaceAll("[\\\\/:%*?\"<>|]", "_")
                .trim();
    }

    // Store upload metadata to database and log upload attempt
    public void saveMetadata(String originalFilename, String generatedFilename, String uploaderUsername,
                             String uploaderRole, long fileSize, String contentType) {
        try {
            FileMetadata metadata = new FileMetadata(
                    sanitizeFilename(originalFilename),
                    generatedFilename,
                    uploaderUsername != null ? uploaderUsername : "ANONYMOUS",
                    uploaderRole != null ? uploaderRole : "USER",
                    fileSize,
                    contentType
            );
            fileMetadataRepository.save(metadata);
            // Log upload attempt
            auditLogService.logAction(uploaderUsername, uploaderRole, "MEDIA_UPLOAD_SUCCESS", "FILE_SECURITY", "/upload", "POST", null, "SUCCESS", "Uploaded file: " + generatedFilename);
        } catch (Exception e) {
            logger.error("Failed to save file metadata: {}", e.getMessage());
        }
    }

    private java.util.Map<String, Object> createErrorResponse(int status, String message) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("status", status);
        response.put("message", message);
        return response;
    }
}
