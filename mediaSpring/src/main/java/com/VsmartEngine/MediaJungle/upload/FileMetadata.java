// ===========================================
// Internship Security Enhancement
// Feature : Secure File Upload Validation
// ISO27001 Control : Secure File Handling
// ===========================================
package com.VsmartEngine.MediaJungle.upload;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// ISO27001 Secure File Handling: FileMetadata JPA Entity mapping table file_metadata
@Entity
@Table(name = "file_metadata")
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "generated_filename")
    private String generatedFilename;

    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    @Column(name = "uploader_username")
    private String uploaderUsername;

    @Column(name = "uploader_role")
    private String uploaderRole;

    @Column(name = "file_size")
    private long fileSize;

    @Column(name = "content_type")
    private String contentType;

    public FileMetadata() {
        this.uploadTime = LocalDateTime.now();
    }

    public FileMetadata(String originalFilename, String generatedFilename, String uploaderUsername,
                        String uploaderRole, long fileSize, String contentType) {
        this.originalFilename = originalFilename;
        this.generatedFilename = generatedFilename;
        this.uploaderUsername = uploaderUsername;
        this.uploaderRole = uploaderRole;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.uploadTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getGeneratedFilename() {
        return generatedFilename;
    }

    public void setGeneratedFilename(String generatedFilename) {
        this.generatedFilename = generatedFilename;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUploaderUsername() {
        return uploaderUsername;
    }

    public void setUploaderUsername(String uploaderUsername) {
        this.uploaderUsername = uploaderUsername;
    }

    public String getUploaderRole() {
        return uploaderRole;
    }

    public void setUploaderRole(String uploaderRole) {
        this.uploaderRole = uploaderRole;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
