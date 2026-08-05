-- ISO 27001 | Module 1: Access Management
-- Database Schema Migration Script for Tasks 1 to 10

-- Task 1, 3, 7: User Security Attributes
ALTER TABLE IF EXISTS user_register ADD COLUMN IF NOT EXISTS role VARCHAR(50) DEFAULT 'USER';
ALTER TABLE IF EXISTS user_register ADD COLUMN IF NOT EXISTS mfa_secret VARCHAR(255);
ALTER TABLE IF EXISTS user_register ADD COLUMN IF NOT EXISTS mfa_enabled BOOLEAN DEFAULT FALSE;
ALTER TABLE IF EXISTS user_register ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'ACTIVE';
ALTER TABLE IF EXISTS user_register ADD COLUMN IF NOT EXISTS account_non_locked BOOLEAN DEFAULT TRUE;
ALTER TABLE IF EXISTS user_register ADD COLUMN IF NOT EXISTS last_login_at TIMESTAMP;

-- Task 2: Access Approval Workflows Table
CREATE TABLE IF NOT EXISTS access_requests (
    id BIGSERIAL PRIMARY KEY,
    requester_email VARCHAR(255) NOT NULL,
    requested_role VARCHAR(100) NOT NULL,
    reason TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    reviewed_by VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP
);

-- Task 4: Privileged Access Management Sessions Table
CREATE TABLE IF NOT EXISTS privileged_sessions (
    id BIGSERIAL PRIMARY KEY,
    admin_email VARCHAR(255) NOT NULL,
    ip_address VARCHAR(100) NOT NULL,
    role VARCHAR(100) NOT NULL,
    elevation_justification TEXT,
    active BOOLEAN DEFAULT TRUE,
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_activity_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP
);

-- Task 5: Active User Sessions Monitoring Table
CREATE TABLE IF NOT EXISTS user_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL,
    token_hash VARCHAR(500),
    ip_address VARCHAR(100),
    user_agent TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_activity_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Task 8: Access Reviews Table
CREATE TABLE IF NOT EXISTS access_reviews (
    id BIGSERIAL PRIMARY KEY,
    target_email VARCHAR(255) NOT NULL,
    reviewed_role VARCHAR(100) NOT NULL,
    reviewer_email VARCHAR(255) NOT NULL,
    decision VARCHAR(50) NOT NULL,
    comments TEXT,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    next_review_due TIMESTAMP
);

-- Task 9: Login Activity Dashboard Table
CREATE TABLE IF NOT EXISTS login_activities (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    status VARCHAR(100) NOT NULL,
    ip_address VARCHAR(100),
    user_agent TEXT,
    failure_reason TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Task 10: Access Audit Logs Repository Table
CREATE TABLE IF NOT EXISTS access_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    user_role VARCHAR(100),
    action VARCHAR(100) NOT NULL,
    resource VARCHAR(255) NOT NULL,
    details TEXT,
    ip_address VARCHAR(100),
    status VARCHAR(50) DEFAULT 'SUCCESS',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
