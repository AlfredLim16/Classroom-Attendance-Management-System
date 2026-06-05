package validations;

import exceptions.ValidationException;

/**
 * Validates fields for enrollment and assignment junction entities:
 * ProfessorCourse, ProfessorSection, and StudentCourse.
 */
public class EnrollmentValidator {

    private EnrollmentValidator() {}

    // --- ProfessorCourse ---

    public static void validateProfessorCourse(Object professor, Object course, Object semester) {
        validateProfessor(professor);
        validateCourse(course);
        validateSemester(semester);
    }

    // --- ProfessorSection ---

    public static void validateProfessorSection(Object professor, Object section, Object semester) {
        validateProfessor(professor);
        validateSection(section);
        validateSemester(semester);
    }

    // --- StudentCourse ---

    public static void validateStudentCourse(Object student, Object course, Object semester) {
        validateStudent(student);
        validateCourse(course);
        validateSemester(semester);
    }

    // --- Shared field validators ---

    public static void validateProfessor(Object professor) {
        if (professor == null) {
            throw new ValidationException("professor", "professor is required");
        }
    }

    public static void validateStudent(Object student) {
        if (student == null) {
            throw new ValidationException("student", "student is required");
        }
    }

    public static void validateCourse(Object course) {
        if (course == null) {
            throw new ValidationException("course", "course is required");
        }
    }

    public static void validateSection(Object section) {
        if (section == null) {
            throw new ValidationException("section", "section is required");
        }
    }

    public static void validateSemester(Object semester) {
        if (semester == null) {
            throw new ValidationException("semester", "semester is required");
        }
    }
}
