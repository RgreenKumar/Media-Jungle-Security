-- ISO 27001 | Module 4: Risk Management
-- Database Schema Migration Script for Tasks 1 to 10

-- Task 1, 2, 7: Risk Register Table
CREATE TABLE IF NOT EXISTS risk_register_items (
    id BIGSERIAL PRIMARY KEY,
    risk_title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100) NOT NULL,
    likelihood INT NOT NULL DEFAULT 3,
    impact INT NOT NULL DEFAULT 3,
    inherent_risk_score INT,
    inherent_risk_level VARCHAR(50),
    control_effectiveness_percent INT DEFAULT 70,
    residual_risk_score INT,
    residual_risk_status VARCHAR(50) DEFAULT 'ACCEPTABLE',
    status VARCHAR(50) NOT NULL DEFAULT 'IDENTIFIED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_assessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Task 4: Risk Mitigation Actions Table
CREATE TABLE IF NOT EXISTS risk_mitigation_actions (
    id BIGSERIAL PRIMARY KEY,
    risk_id BIGINT NOT NULL,
    mitigation_title VARCHAR(255) NOT NULL,
    action_details TEXT,
    assigned_owner VARCHAR(255) NOT NULL,
    target_completion_date DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'PLANNED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Task 5: STRIDE Threat Analysis Entries Table
CREATE TABLE IF NOT EXISTS threat_analysis_entries (
    id BIGSERIAL PRIMARY KEY,
    threat_name VARCHAR(255) NOT NULL,
    stride_category VARCHAR(100) NOT NULL,
    target_asset VARCHAR(255) NOT NULL,
    threat_vector_details TEXT,
    likelihood VARCHAR(50) DEFAULT 'MEDIUM',
    identified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Task 6: Control Effectiveness Reviews Table
CREATE TABLE IF NOT EXISTS control_effectiveness_reviews (
    id BIGSERIAL PRIMARY KEY,
    control_name VARCHAR(255) NOT NULL,
    effectiveness_percent INT DEFAULT 85,
    evaluation_notes TEXT,
    tested_by VARCHAR(255),
    test_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Task 10: Risk Review Sign-Off Records Table
CREATE TABLE IF NOT EXISTS risk_review_records (
    id BIGSERIAL PRIMARY KEY,
    reviewer_email VARCHAR(255) NOT NULL,
    review_period VARCHAR(100) NOT NULL,
    total_risks_reviewed INT,
    high_critical_count INT,
    ciso_signoff_comments TEXT,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    next_review_due TIMESTAMP
);
