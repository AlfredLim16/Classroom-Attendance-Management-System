package validations;

import exceptions.InvalidSessionException;
import exceptions.ValidationException;
import lookup.ContextType;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Validates fields for the ClassSession entity before persistence.
 */
public class ClassSessionValidator {

    private ClassSessionValidator(){
    }

    public static void validate(Object course, Object section, Object professor, LocalDate sessionDate, LocalTime startTime, LocalTime endTime, Object contextType){
        validateCourse(course);
        validateSection(section);
        validateProfessor(professor);
        validateSessionDate(sessionDate);
        validateTimeRange(startTime, endTime);
        validateContextType(contextType);
    }

    public static void validateEventContext(Object contextType, Object event) {
        if (contextType == ContextType.SCHOOL_EVENT && event == null) {
            throw new ValidationException("event", "event is required when contextType is SCHOOL_EVENT");
        }
    }

    public static void validateCourse(Object course){
        if(course == null){
            throw new ValidationException("course", "course is required");
        }
    }

    public static void validateSection(Object section){
        if(section == null){
            throw new ValidationException("section", "section is required");
        }
    }

    public static void validateProfessor(Object professor){
        if(professor == null){
            throw new ValidationException("professor", "professor is required");
        }
    }

    public static void validateSessionDate(LocalDate sessionDate){
        if(sessionDate == null){
            throw new ValidationException("sessionDate", "sessionDate is required");
        }
    }

    public static void validateTimeRange(LocalTime startTime, LocalTime endTime){
        if(startTime == null){
            throw new ValidationException("startTime", "startTime is required");
        }
        if(endTime == null){
            throw new ValidationException("endTime", "endTime is required");
        }
        if(!endTime.isAfter(startTime)){
            throw new InvalidSessionException(
                "endTime (" + endTime + ") must be after startTime (" + startTime + ")");
        }
    }

    public static void validateContextType(Object contextType){
        if(contextType == null){
            throw new ValidationException("contextType", "contextType is required");
        }
    }
}
