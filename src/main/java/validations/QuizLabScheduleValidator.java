package validations;

import exceptions.ValidationException;
import lookup.MissedQuizStatus;
import java.time.LocalDate;

/**
 * Validates fields for the QuizLabSchedule and MissedQuizFlag entities before persistence.
 */
public class QuizLabScheduleValidator {

    private QuizLabScheduleValidator() {}

    // --- QuizLabSchedule ---

    public static void validateSchedule(Object course, LocalDate quizDate, Object quizType) {
        validateCourse(course);
        validateQuizDate(quizDate);
        validateQuizType(quizType);
    }

    public static void validateCourse(Object course) {
        if (course == null) {
            throw new ValidationException("course", "course is required");
        }
    }

    public static void validateQuizDate(LocalDate quizDate) {
        if (quizDate == null) {
            throw new ValidationException("quizDate", "quizDate is required");
        }
    }

    public static void validateQuizType(Object quizType) {
        if (quizType == null) {
            throw new ValidationException("quizType", "quizType is required");
        }
    }

    // --- MissedQuizFlag ---

    public static void validateMissedQuizFlag(Object student, Object quiz,
                                              Object missedQuizStatus, Object decisionType,
                                              Object decidedByProfessor) {
        validateFlagStudent(student);
        validateFlagQuiz(quiz);
        validateMissedQuizStatus(missedQuizStatus);
        validateDecisionConsistency(missedQuizStatus, decisionType, decidedByProfessor);
    }

    public static void validateFlagStudent(Object student) {
        if (student == null) {
            throw new ValidationException("student", "student is required");
        }
    }

    public static void validateFlagQuiz(Object quiz) {
        if (quiz == null) {
            throw new ValidationException("quiz", "quiz is required");
        }
    }

    public static void validateMissedQuizStatus(Object missedQuizStatus) {
        if (missedQuizStatus == null) {
            throw new ValidationException("missedQuizStatus", "missedQuizStatus is required");
        }
    }

    /**
     * Decision fields are only required once a decision has been made (status is not PENDING).
     */
    public static void validateDecisionConsistency(Object missedQuizStatus, Object decisionType,
                                                    Object decidedByProfessor) {
        if (missedQuizStatus == MissedQuizStatus.PENDING) {
            return; // no decision yet — both fields are allowed to be null
        }
        if (decisionType == null) {
            throw new ValidationException("decisionType", "decisionType is required once a decision is made");
        }
        if (decidedByProfessor == null) {
            throw new ValidationException("decidedByProfessor", "decidedByProfessor is required once a decision is made");
        }
    }
}
