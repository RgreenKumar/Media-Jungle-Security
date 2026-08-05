-- ISO 27001 | Module 2: Data Backup & Recovery
-- Database Schema Migration Script for Tasks 1 to 10

-- Task 1, 3, 4, 10: Backup Archive Records Table
CREATE TABLE IF NOT EXISTS backup_records (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    file_path TEXT NOT NULL,
    file_size_bytes BIGINT,
    encrypted BOOLEAN DEFAULT TRUE,
    encryption_algorithm VARCHAR(100) DEFAULT 'AES-256-GCM',
    checksum_sha256 VARCHAR(255),
    verification_status VARCHAR(50) DEFAULT 'UNVERIFIED',
    last_verified_at TIMESTAMP,
    retention_days INT DEFAULT 30,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Task 6: Recovery Test Logs Table
CREATE TABLE IF NOT EXISTS recovery_test_logs (
    id BIGSERIAL PRIMARY KEY,
    backup_record_id BIGINT NOT NULL,
    backup_filename VARCHAR(255),
    duration_seconds BIGINT,
    status VARCHAR(50) NOT NULL DEFAULT 'SUCCESS',
    test_notes TEXT,
    performed_by VARCHAR(255),
    test_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Task 5, 7, 8: Disaster Recovery & RTO / RPO Metrics Table
CREATE TABLE IF NOT EXISTS dr_metrics (
    id BIGSERIAL PRIMARY KEY,
    target_rto_minutes BIGINT DEFAULT 240,
    actual_rto_minutes BIGINT DEFAULT 45,
    rto_status VARCHAR(50) DEFAULT 'COMPLIANT',
    target_rpo_minutes BIGINT DEFAULT 60,
    actual_rpo_lag_minutes BIGINT DEFAULT 15,
    rpo_status VARCHAR(50) DEFAULT 'COMPLIANT',
    readiness_score_percent INT DEFAULT 98,
    dr_site_status VARCHAR(100) DEFAULT 'READY_FAILOVER',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Task 9: Business Continuity Tracker Items Table
CREATE TABLE IF NOT EXISTS bcp_items (
    id BIGSERIAL PRIMARY KEY,
    component_name VARCHAR(255) NOT NULL,
    criticality_level VARCHAR(50) DEFAULT 'HIGH',
    health_status VARCHAR(50) DEFAULT 'OPERATIONAL',
    failover_plan TEXT,
    last_health_check TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
