package com.VsmartEngine.MediaJungle.backup.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ISO 27001 | Module 2: Data Backup & Recovery | Task 1: Backup Management Module
// Description: BackupRecord entity tracking backup archive metadata, file path, size, encryption status, and retention expiration date.
@Entity
@Table(name = "backup_records")
public class BackupRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String filePath;

    private Long fileSizeBytes;

    // ISO 27001 | Module 2: Data Backup & Recovery | Task 3: Backup Encryption
    // Description: Encryption algorithm name and hash algorithm utilized for securing backup data.
    private Boolean encrypted = true;

    private String encryptionAlgorithm = "AES-256-GCM";

    // ISO 27001 | Module 2: Data Backup & Recovery | Task 4: Backup Verification
    // Description: SHA-256 checksum and verification status for verifying backup integrity.
    private String checksumSha256;

    private String verificationStatus = "UNVERIFIED"; // UNVERIFIED, VERIFIED, CORRUPTED

    private LocalDateTime lastVerifiedAt;

    // ISO 27001 | Module 2: Data Backup & Recovery | Task 10: Backup Retention Management
    // Description: Retention lifecycle period in days and calculated expiration timestamp.
    private Integer retentionDays = 30;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt = LocalDateTime.now();

    public BackupRecord() {}

    public BackupRecord(String filename, String filePath, Long fileSizeBytes, String checksumSha256, Integer retentionDays) {
        this.filename = filename;
        this.filePath = filePath;
        this.fileSizeBytes = fileSizeBytes;
        this.checksumSha256 = checksumSha256;
        this.retentionDays = retentionDays != null ? retentionDays : 30;
        this.expiresAt = LocalDateTime.now().plusDays(this.retentionDays);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public Long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }

    public Boolean getEncrypted() { return encrypted; }
    public void setEncrypted(Boolean encrypted) { this.encrypted = encrypted; }

    public String getEncryptionAlgorithm() { return encryptionAlgorithm; }
    public void setEncryptionAlgorithm(String encryptionAlgorithm) { this.encryptionAlgorithm = encryptionAlgorithm; }

    public String getChecksumSha256() { return checksumSha256; }
    public void setChecksumSha256(String checksumSha256) { this.checksumSha256 = checksumSha256; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public LocalDateTime getLastVerifiedAt() { return lastVerifiedAt; }
    public void setLastVerifiedAt(LocalDateTime lastVerifiedAt) { this.lastVerifiedAt = lastVerifiedAt; }

    public Integer getRetentionDays() { return retentionDays; }
    public void setRetentionDays(Integer retentionDays) { this.retentionDays = retentionDays; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
