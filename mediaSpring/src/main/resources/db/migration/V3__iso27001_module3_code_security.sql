-- ISO 27001 | Module 3: Code Level Security
-- Database Schema Migration Script for Tasks 1 to 10

-- Task 7: Dependency Vulnerabilities Management Table
CREATE TABLE IF NOT EXISTS dependency_vulnerabilities (
    id BIGSERIAL PRIMARY KEY,
    package_name VARCHAR(255) NOT NULL,
    current_version VARCHAR(100),
    cve_id VARCHAR(100),
    severity VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    project_module VARCHAR(100),
    remediation_recommendation TEXT,
    patched BOOLEAN DEFAULT FALSE,
    discovered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Task 8: Security Code Reviews Table
CREATE TABLE IF NOT EXISTS code_reviews (
    id BIGSERIAL PRIMARY KEY,
    feature_title VARCHAR(255) NOT NULL,
    author_email VARCHAR(255) NOT NULL,
    reviewer_email VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    security_checklist_comments TEXT,
    static_analysis_passed BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP
);

-- Task 9: Security Scan Test Runs Table
CREATE TABLE IF NOT EXISTS security_test_runs (
    id BIGSERIAL PRIMARY KEY,
    test_type VARCHAR(100) NOT NULL,
    total_checks_performed INT,
    vulnerabilities_found_count INT,
    critical_count INT DEFAULT 0,
    high_count INT DEFAULT 0,
    summary_report TEXT,
    status VARCHAR(50) DEFAULT 'PASSED',
    run_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Task 10: Vulnerability Management Portal Table
CREATE TABLE IF NOT EXISTS vulnerabilities (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    vulnerability_type VARCHAR(100),
    cvss_score DOUBLE PRECISION DEFAULT 5.0,
    severity VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    assigned_to VARCHAR(255),
    affected_component VARCHAR(100),
    reported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP
);
