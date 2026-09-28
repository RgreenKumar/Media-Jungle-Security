// =======================================
// Internship Security Enhancement
// Feature: Role Based Access Control
// ISO27001 Control: Access Control
// =======================================
package com.VsmartEngine.MediaJungle.security;

// RBAC
// Enum representing system roles for access control
public enum UserRole {
    // Only Admin Allowed
    ADMIN,
    
    // Employee & Admin Access
    EMPLOYEE,
    
    // Standard User Access
    USER;

    // RBAC: Convert string to enum safely
    public static UserRole fromString(String roleStr) {
        if (roleStr == null || roleStr.trim().isEmpty()) {
            return USER; // Prevent Privilege Escalation: Default fallback role
        }
        try {
            return UserRole.valueOf(roleStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return USER; // Prevent Privilege Escalation: Default fallback role
        }
    }
}
