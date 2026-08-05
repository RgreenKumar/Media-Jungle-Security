package com.VsmartEngine.MediaJungle.backup.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

// ISO 27001 | Module 2: Data Backup & Recovery | Task 4: Backup Verification
// Description: Utility for calculating SHA-256 checksums and verifying backup file integrity against corruption.
public class BackupVerificationUtil {

    public static String calculateSha256(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String calculateSha256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] byteArray = new byte[8192];
            int bytesCount = 0;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static boolean verifyFileIntegrity(File file, String expectedSha256) {
        if (!file.exists() || file.length() == 0) return false;
        try {
            String actualSha256 = calculateSha256(file);
            return actualSha256.equalsIgnoreCase(expectedSha256);
        } catch (Exception e) {
            return false;
        }
    }
}
