package validations;

import exceptions.ValidationException;

/**
 * Validates fields for the Course entity before persistence.
 */
public class CourseValidator {

    private CourseValidator() {}

    public static void validate(Object program, String courseCode, String courseName,
                                int units, Object semester, Object yearLevel) {
        validateProgram(program);
        validateCourseCode(courseCode);
        validateCourseName(courseName);
        validateUnits(units);
        validateSemester(semester);
        validateYearLevel(yearLevel);
    }

    public static void validateProgram(Object program) {
        if (program == null) {
            throw new ValidationException("program", "program is required");
        }
    }

    public static void validateCourseCode(String courseCode) {
        if (courseCode == null || courseCode.isBlank()) {
            throw new ValidationException("courseCode", "courseCode is required");
        }
        if (courseCode.length() > 50) {
            throw new ValidationException("courseCode", "courseCode must not exceed 50 characters");
        }
    }

    public static void validateCourseName(String courseName) {
        if (courseName == null || courseName.isBlank()) {
            throw new ValidationException("courseName", "courseName is required");
        }
        if (courseName.length() > 150) {
            throw new ValidationException("courseName", "courseName must not exceed 150 characters");
        }
    }

    public static void validateUnits(int units) {
        if (units < 1) {
            throw new ValidationException("units", "units must be at least 1");
        }
        if (units > 6) {
            throw new ValidationException("units", "units must not exceed 6");
        }
    }

    public static void validateSemester(Object semester) {
        if (semester == null) {
            throw new ValidationException("semester", "semester is required");
        }
    }

    public static void validateYearLevel(Object yearLevel) {
        if (yearLevel == null) {
            throw new ValidationException("yearLevel", "yearLevel is required");
        }
    }
}
