package validations;

import exceptions.ValidationException;
import java.time.LocalDate;

/**
 * Validates fields for the Semester entity before persistence.
 */
public class SemesterValidator {

    private SemesterValidator() {}

    public static void validate(String semesterName, String schoolYear,
                                LocalDate startDate, LocalDate endDate) {
        validateSemesterName(semesterName);
        validateSchoolYear(schoolYear);
        validateDateRange(startDate, endDate);
    }

    public static void validateSemesterName(String semesterName) {
        if (semesterName == null || semesterName.isBlank()) {
            throw new ValidationException("semesterName", "semesterName is required");
        }
        if (semesterName.length() > 50) {
            throw new ValidationException("semesterName", "semesterName must not exceed 50 characters");
        }
    }

    public static void validateSchoolYear(String schoolYear) {
        if (schoolYear == null || schoolYear.isBlank()) {
            throw new ValidationException("schoolYear", "schoolYear is required");
        }
        if (schoolYear.length() > 20) {
            throw new ValidationException("schoolYear", "schoolYear must not exceed 20 characters");
        }
    }

    public static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new ValidationException("startDate", "startDate is required");
        }
        if (endDate == null) {
            throw new ValidationException("endDate", "endDate is required");
        }
        if (!endDate.isAfter(startDate)) {
            throw new ValidationException("endDate", "endDate must be after startDate");
        }
    }
}
