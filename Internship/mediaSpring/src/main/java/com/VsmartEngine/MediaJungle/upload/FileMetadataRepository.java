// ===========================================
// Internship Security Enhancement
// Feature : Secure File Upload Validation
// ISO27001 Control : Secure File Handling
// ===========================================
package com.VsmartEngine.MediaJungle.upload;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ISO27001 Secure File Handling: FileMetadataRepository for persisting uploaded file details
@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
}
