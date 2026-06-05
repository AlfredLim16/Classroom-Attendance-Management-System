package validations;

import exceptions.ValidationException;

/**
 * Validates fields for the Student entity before persistence.
 */
public class StudentValidator {

    private StudentValidator() {}

    public static void validate(Object user, String studentNumber, String firstName,
                                String middleName, String lastName,
                                Object program, Object yearLevel, Object section) {
        validateUser(user);
        validateStudentNumber(studentNumber);
        validateFirstName(firstName);
        validateLastName(lastName);
        validateProgram(program);
        validateYearLevel(yearLevel);
        validateSection(section);
    }

    public static void validateUser(Object user) {
        if (user == null) {
            throw new ValidationException("user", "user is required");
        }
    }

    public static void validateStudentNumber(String studentNumber) {
        if (studentNumber == null || studentNumber.isBlank()) {
            throw new ValidationException("studentNumber", "studentNumber is required");
        }
        if (studentNumber.length() > 50) {
            throw new ValidationException("studentNumber", "studentNumber must not exceed 50 characters");
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

    public static void validateProgram(Object program) {
        if (program == null) {
            throw new ValidationException("program", "program is required");
        }
    }

    public static void validateYearLevel(Object yearLevel) {
        if (yearLevel == null) {
            throw new ValidationException("yearLevel", "yearLevel is required");
        }
    }

    public static void validateSection(Object section) {
        if (section == null) {
            throw new ValidationException("section", "section is required");
        }
    }
}
