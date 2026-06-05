package validations;

import exceptions.ValidationException;

/**
 * Validates fields for the Attendance and AttendancePolicy entities before persistence.
 */
public class AttendanceValidator {

    private AttendanceValidator() {}

    // --- Attendance ---

    public static void validateAttendance(Object session, Object student,
                                          Object status, Object recordedBy) {
        validateSession(session);
        validateStudent(student);
        validateStatus(status);
        validateRecordedBy(recordedBy);
    }

    public static void validateSession(Object session) {
        if (session == null) {
            throw new ValidationException("session", "session is required");
        }
    }

    public static void validateStudent(Object student) {
        if (student == null) {
            throw new ValidationException("student", "student is required");
        }
    }

    public static void validateStatus(Object status) {
        if (status == null) {
            throw new ValidationException("status", "attendance status is required");
        }
    }

    public static void validateRecordedBy(Object recordedBy) {
        if (recordedBy == null) {
            throw new ValidationException("recordedBy", "recordedBy user is required");
        }
    }

    // --- AttendancePolicy ---

    public static void validatePolicy(Object course, int lateThresholdMinutes,
                                      int latesEqualToAbsent, int absentsEqualToDropped) {
        validatePolicyCourse(course);
        validateLateThreshold(lateThresholdMinutes);
        validateLatesEqualToAbsent(latesEqualToAbsent);
        validateAbsentsEqualToDropped(absentsEqualToDropped);
    }

    public static void validatePolicyCourse(Object course) {
        if (course == null) {
            throw new ValidationException("course", "course is required for attendance policy");
        }
    }

    public static void validateLateThreshold(int lateThresholdMinutes) {
        if (lateThresholdMinutes < 0) {
            throw new ValidationException("lateThresholdMinutes", "lateThresholdMinutes cannot be negative");
        }
    }

    public static void validateLatesEqualToAbsent(int latesEqualToAbsent) {
        if (latesEqualToAbsent < 1) {
            throw new ValidationException("latesEqualToAbsent", "latesEqualToAbsent must be at least 1");
        }
    }

    public static void validateAbsentsEqualToDropped(int absentsEqualToDropped) {
        if (absentsEqualToDropped < 1) {
            throw new ValidationException("absentsEqualToDropped", "absentsEqualToDropped must be at least 1");
        }
    }
}
