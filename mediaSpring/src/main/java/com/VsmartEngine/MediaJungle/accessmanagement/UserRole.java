package com.VsmartEngine.MediaJungle.accessmanagement;

// ISO 27001 | Module 1: Access Management | Task 1: User Role Management
// Description: Defines security-specific role classifications (SECURITY_ADMIN, SYSTEM_ADMIN, AUDITOR, USER, ADMIN) for fine-grained role-based access control (RBAC).
public enum UserRole {
    USER,
    SECURITY_ADMIN,
    SYSTEM_ADMIN,
    AUDITOR,
    ADMIN
}
