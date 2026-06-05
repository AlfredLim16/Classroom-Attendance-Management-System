package junction;

import core.Course;
import lookup.QuizType;
import validations.QuizLabScheduleValidator;
import java.time.LocalDate;
import java.util.Objects;

public record QuizLabSchedule(int quizId, Course course, LocalDate quizDate, QuizType quizType) {

    public QuizLabSchedule {
        QuizLabScheduleValidator.validateSchedule(course, quizDate, quizType);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        QuizLabSchedule that = (QuizLabSchedule) object;
        return quizId == that.quizId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quizId);
    }

    @Override
    public String toString() {
        return "QuizLabSchedule{id=" + quizId + ", date=" + quizDate + ", type=" + quizType + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int quizId;
        private Course course;
        private LocalDate quizDate;
        private QuizType quizType;

        public Builder quizId(int quizId) {
            this.quizId = quizId;
            return this;
        }

        public Builder course(Course course) {
            this.course = course;
            return this;
        }

        public Builder quizDate(LocalDate quizDate) {
            this.quizDate = quizDate;
            return this;
        }

        public Builder quizType(QuizType quizType) {
            this.quizType = quizType;
            return this;
        }

        public QuizLabSchedule build() {
            return new QuizLabSchedule(quizId, course, quizDate, quizType);
        }
    }
}
