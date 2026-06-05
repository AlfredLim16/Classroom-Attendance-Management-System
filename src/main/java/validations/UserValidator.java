package validations;

import exceptions.ValidationException;

/**
 * Validates fields for the User entity before persistence.
 */
public class UserValidator {

    private UserValidator() {}

    public static void validate(String userName, String userPassword, Object role) {
        validateUserName(userName);
        validateUserPassword(userPassword);
        validateRole(role);
    }

    public static void validateUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new ValidationException("userName", "userName is required");
        }
        if (userName.length() > 100) {
            throw new ValidationException("userName", "userName must not exceed 100 characters");
        }
    }

    /**
     * Validates a raw (plain-text) password at registration or password change time.
     * Enforces minimum length rules before hashing.
     */
    public static void validateRawPassword(String userPassword) {
        if (userPassword == null || userPassword.isBlank()) {
            throw new ValidationException("userPassword", "userPassword is required");
        }
        if (userPassword.length() < 6) {
            throw new ValidationException("userPassword", "userPassword must be at least 6 characters");
        }
    }

    /**
     * Validates a hashed password loaded from the database.
     * Only checks that the value is present — length rules do not apply to hashes.
     */
    public static void validateUserPassword(String userPassword) {
        if (userPassword == null || userPassword.isBlank()) {
            throw new ValidationException("userPassword", "userPassword is required");
        }
    }

    public static void validateRole(Object role) {
        if (role == null) {
            throw new ValidationException("role", "role is required");
        }
    }
}
