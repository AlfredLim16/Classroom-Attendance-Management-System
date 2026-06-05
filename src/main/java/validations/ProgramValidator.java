package validations;

import exceptions.ValidationException;

/**
 * Validates fields for the Program entity before persistence.
 */
public class ProgramValidator {

    private ProgramValidator() {}

    public static void validate(String programName) {
        validateProgramName(programName);
    }

    public static void validateProgramName(String programName) {
        if (programName == null || programName.isBlank()) {
            throw new ValidationException("programName", "programName is required");
        }
        if (programName.length() > 100) {
            throw new ValidationException("programName", "programName must not exceed 100 characters");
        }
    }
}
