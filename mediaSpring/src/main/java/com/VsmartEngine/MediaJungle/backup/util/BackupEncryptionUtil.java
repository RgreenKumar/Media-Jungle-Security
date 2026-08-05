package com.VsmartEngine.MediaJungle.backup.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

// ISO 27001 | Module 2: Data Backup & Recovery | Task 3: Backup Encryption
// Description: Utility for encrypting and decrypting backup dump archives using AES-256-GCM symmetric encryption before storage.
public class BackupEncryptionUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH_BYTES = 12;
    private static final String DEFAULT_SECRET = "ISO27001_MediaJungle_Backup_Encryption_Secret_Key_256Bits!";

    private static SecretKey deriveKey(String secret) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static byte[] encrypt(byte[] plainData, String secretKeyStr) throws Exception {
        String keyToUse = (secretKeyStr != null && !secretKeyStr.isEmpty()) ? secretKeyStr : DEFAULT_SECRET;
        SecretKey key = deriveKey(keyToUse);

        byte[] iv = new byte[IV_LENGTH_BYTES];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

        byte[] encryptedData = cipher.doFinal(plainData);

        // Prepend IV to encrypted bytes
        byte[] combined = new byte[IV_LENGTH_BYTES + encryptedData.length];
        System.arraycopy(iv, 0, combined, 0, IV_LENGTH_BYTES);
        System.arraycopy(encryptedData, 0, combined, IV_LENGTH_BYTES, encryptedData.length);

        return combined;
    }

    public static byte[] decrypt(byte[] cipherDataWithIv, String secretKeyStr) throws Exception {
        String keyToUse = (secretKeyStr != null && !secretKeyStr.isEmpty()) ? secretKeyStr : DEFAULT_SECRET;
        SecretKey key = deriveKey(keyToUse);

        byte[] iv = Arrays.copyOfRange(cipherDataWithIv, 0, IV_LENGTH_BYTES);
        byte[] cipherData = Arrays.copyOfRange(cipherDataWithIv, IV_LENGTH_BYTES, cipherDataWithIv.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

        return cipher.doFinal(cipherData);
    }
}
