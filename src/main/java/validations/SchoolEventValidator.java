package validations;

import exceptions.ValidationException;
import java.time.LocalDate;

/**
 * Validates fields for the SchoolEvent entity before persistence.
 */
public class SchoolEventValidator {

    private SchoolEventValidator() {}

    public static void validate(String eventName, LocalDate eventDate) {
        validateEventName(eventName);
        validateEventDate(eventDate);
    }

    public static void validateEventName(String eventName) {
        if (eventName == null || eventName.isBlank()) {
            throw new ValidationException("eventName", "eventName is required");
        }
        if (eventName.length() > 150) {
            throw new ValidationException("eventName", "eventName must not exceed 150 characters");
        }
    }

    public static void validateEventDate(LocalDate eventDate) {
        if (eventDate == null) {
            throw new ValidationException("eventDate", "eventDate is required");
        }
    }
}
