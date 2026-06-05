package validations;

import exceptions.ValidationException;

/**
 * Validates fields for the Section and Secretary entities before persistence.
 */
public class SectionValidator {

    private SectionValidator() {}

    // --- Section ---

    public static void validateSection(Object program, Object yearLevel, String sectionCode) {
        validateProgram(program);
        validateYearLevel(yearLevel);
        validateSectionCode(sectionCode);
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

    public static void validateSectionCode(String sectionCode) {
        if (sectionCode == null || sectionCode.isBlank()) {
            throw new ValidationException("sectionCode", "sectionCode is required");
        }
        if (sectionCode.length() > 20) {
            throw new ValidationException("sectionCode", "sectionCode must not exceed 20 characters");
        }
    }

    // --- Secretary ---

    public static void validateSecretary(Object student, Object section) {
        validateSecretaryStudent(student);
        validateSecretarySection(section);
    }

    public static void validateSecretaryStudent(Object student) {
        if (student == null) {
            throw new ValidationException("student", "student is required");
        }
    }

    public static void validateSecretarySection(Object section) {
        if (section == null) {
            throw new ValidationException("section", "section is required");
        }
    }
}
