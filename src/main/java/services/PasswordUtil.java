package services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_BYTES = 16;
    private static final String DELIMITER = ":";

    private PasswordUtil(){
    }

    public static String hash(String rawPassword){
        if(rawPassword == null || rawPassword.isBlank()){
            throw new IllegalArgumentException("Password must not be blank");
        }
        byte[] salt = generateSalt();
        byte[] hash = sha256(salt, rawPassword);
        return Base64.getEncoder().encodeToString(salt)
            + DELIMITER
            + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verify(String rawPassword, String stored){
        if(rawPassword == null || stored == null){
            return false;
        }
        String[] parts = stored.split(DELIMITER, 2);
        if(parts.length != 2){
            return false;
        }
        try{
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
            byte[] actualHash = sha256(salt, rawPassword);
            return MessageDigest.isEqual(expectedHash, actualHash);
        }catch(IllegalArgumentException e){
            return false;
        }
    }

    private static byte[] generateSalt(){
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private static byte[] sha256(byte[] salt, String password){
        try{
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            return md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }catch(NoSuchAlgorithmException e){
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
