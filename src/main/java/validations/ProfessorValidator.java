package validations;

import exceptions.ValidationException;

/**
 * Validates fields for the Professor entity before persistence.
 */
public class ProfessorValidator {

    private ProfessorValidator() {}

    public static void validate(Object user, String firstName, String middleName,
                                String lastName, Object professorType) {
        validateUser(user);
        validateFirstName(firstName);
        validateLastName(lastName);
        validateProfessorType(professorType);
    }

    public static void validateUser(Object user) {
        if (user == null) {
            throw new ValidationException("user", "user is required");
        }
    }

    public static void validateFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            throw new ValidationException("firstName", "firstName is required");
        }
        if (firstName.length() > 100) {
            throw new ValidationException("firstName", "firstName must not exceed 100 characters");
        }
    }

    public static void validateLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new ValidationException("lastName", "lastName is required");
        }
        if (lastName.length() > 100) {
            throw new ValidationException("lastName", "lastName must not exceed 100 characters");
        }
    }

    public static void validateProfessorType(Object professorType) {
        if (professorType == null) {
            throw new ValidationException("professorType", "professorType is required");
        }
    }
}
