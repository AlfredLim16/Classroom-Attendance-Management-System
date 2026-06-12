package services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing and verification using SHA-256 with a random salt.
 *
 * Format stored in DB:  <base64-salt>:<base64-hash>
 *
 * SHA-256 + salt is a significant improvement over plaintext and requires no
 * additional Maven dependencies.  For a production system, BCrypt (via
 * spring-security-crypto or jBCrypt) would be preferred, but this is sufficient
 * for a demo/academic context.
 */
public final class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_BYTES = 16;
    private static final String DELIMITER = ":";

    private PasswordUtil() {}

    /**
     * Hashes a raw password with a freshly generated random salt.
     *
     * @param rawPassword plain-text password (must not be null or blank)
     * @return  storable string in the format  salt:hash  (both Base64-encoded)
     */
    public static String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        byte[] salt = generateSalt();
        byte[] hash = sha256(salt, rawPassword);
        return Base64.getEncoder().encodeToString(salt)
                + DELIMITER
                + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Verifies a raw password against a stored salt:hash string.
     *
     * @param rawPassword  plain-text password entered by the user
     * @param stored       value retrieved from the database (salt:hash)
     * @return true if the password matches
     */
    public static boolean verify(String rawPassword, String stored) {
        if (rawPassword == null || stored == null) return false;
        String[] parts = stored.split(DELIMITER, 2);
        if (parts.length != 2) return false;            // legacy plaintext — reject
        try {
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
            byte[] actualHash = sha256(salt, rawPassword);
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // ── private helpers ───────────────────────────────────────────────────

    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private static byte[] sha256(byte[] salt, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            return md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed to be present in every JVM
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
