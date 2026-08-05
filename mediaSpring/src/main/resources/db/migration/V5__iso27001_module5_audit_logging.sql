-- ISO 27001 | Module 5: Audit Logging & Monitoring
-- Database Schema Migration Script for Tasks 1 to 10

-- Task 1: User Activity Logs Table
CREATE TABLE IF NOT EXISTS user_activity_logs (
    id BIGSERIAL PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL,
    action VARCHAR(100) NOT NULL,
    target_resource VARCHAR(255),
    ip_address VARCHAR(100),
    user_agent VARCHAR(500),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Task 2: Administrative Action Logs Table
CREATE TABLE IF NOT EXISTS admin_action_logs (
    id BIGSERIAL PRIMARY KEY,
    admin_email VARCHAR(255) NOT NULL,
    action_type VARCHAR(100) NOT NULL,
    target_setting VARCHAR(255) NOT NULL,
    change_details TEXT,
    ip_address VARCHAR(100),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Task 3: Central Audit Trail Entries Table
CREATE TABLE IF NOT EXISTS audit_trail_entries (
    id BIGSERIAL PRIMARY KEY,
    event_category VARCHAR(100) NOT NULL,
    actor VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    target_resource VARCHAR(255) NOT NULL,
    payload_diff TEXT,
    ip_address VARCHAR(100),
    status VARCHAR(50) DEFAULT 'SUCCESS',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Task 4: Security Incident & Threat Event Logs Table
CREATE TABLE IF NOT EXISTS security_event_logs (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    severity VARCHAR(50) NOT NULL DEFAULT 'WARNING',
    event_details TEXT,
    source_ip VARCHAR(100),
    user_email VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Task 6: Security Alerts Table
CREATE TABLE IF NOT EXISTS security_alerts (
    id BIGSERIAL PRIMARY KEY,
    alert_title VARCHAR(255) NOT NULL,
    severity VARCHAR(50) NOT NULL DEFAULT 'HIGH',
    alert_summary TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    assigned_to VARCHAR(255),
    triggered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP
);

-- Task 10: Audit Review Sign-Off Records Table
CREATE TABLE IF NOT EXISTS audit_review_records (
    id BIGSERIAL PRIMARY KEY,
    auditor_email VARCHAR(255) NOT NULL,
    audit_scope VARCHAR(100) NOT NULL,
    total_logs_reviewed INT,
    review_findings TEXT,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    next_review_due TIMESTAMP
);
