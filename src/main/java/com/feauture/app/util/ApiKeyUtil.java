package com.feauture.app.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

public class ApiKeyUtil {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateKey() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return "fk_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String hashKey(String rawKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawKey.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}