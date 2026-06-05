package validations;

import exceptions.ValidationException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Validates fields for the ExcuseLetter entity before persistence.
 */
public class ExcuseLetterValidator {

    private ExcuseLetterValidator() {}

    public static void validate(Object student, Object course, LocalDate absentDate,
                                String reason, Object status, LocalDateTime submittedDate) {
        validateStudent(student);
        validateCourse(course);
        validateAbsentDate(absentDate);
        validateReason(reason);
        validateStatus(status);
        validateSubmittedDate(submittedDate);
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

    public static void validateAbsentDate(LocalDate absentDate) {
        if (absentDate == null) {
            throw new ValidationException("absentDate", "absentDate is required");
        }
        if (absentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("absentDate", "absentDate cannot be in the future");
        }
    }

    public static void validateReason(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new ValidationException("reason", "reason is required");
        }
        if (reason.length() > 1000) {
            throw new ValidationException("reason", "reason must not exceed 1000 characters");
        }
    }

    public static void validateStatus(Object status) {
        if (status == null) {
            throw new ValidationException("status", "excuse status is required");
        }
    }

    public static void validateSubmittedDate(LocalDateTime submittedDate) {
        if (submittedDate == null) {
            throw new ValidationException("submittedDate", "submittedDate is required");
        }
    }
}
