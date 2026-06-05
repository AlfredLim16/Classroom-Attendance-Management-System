package junction;

import core.Course;
import core.Professor;
import core.SchoolEvent;
import core.Section;
import lookup.ContextType;
import validations.ClassSessionValidator;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public record ClassSession(
    int sessionId, Course course, Section section, Professor professor, LocalDate sessionDate, LocalTime startTime, LocalTime endTime, ContextType contextType, SchoolEvent event) {

    public ClassSession {
        ClassSessionValidator.validate(course, section, professor, sessionDate, startTime, endTime, contextType);
        ClassSessionValidator.validateEventContext(contextType, event);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ClassSession that = (ClassSession) object;
        return sessionId == that.sessionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }

    @Override
    public String toString() {
        return "ClassSession{id=" + sessionId + ", date=" + sessionDate + ", course=" + (course != null ? course.courseCode() : "null") + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int sessionId;
        private Course course;
        private Section section;
        private Professor professor;
        private LocalDate sessionDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private ContextType contextType;
        private SchoolEvent event;

        public Builder sessionId(int sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder course(Course course) {
            this.course = course;
            return this;
        }

        public Builder section(Section section) {
            this.section = section;
            return this;
        }

        public Builder professor(Professor professor) {
            this.professor = professor;
            return this;
        }

        public Builder sessionDate(LocalDate sessionDate) {
            this.sessionDate = sessionDate;
            return this;
        }

        public Builder startTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder contextType(ContextType contextType) {
            this.contextType = contextType;
            return this;
        }

        public Builder event(SchoolEvent event) {
            this.event = event;
            return this;
        }

        public ClassSession build() {
            return new ClassSession(sessionId, course, section, professor, sessionDate, startTime, endTime, contextType, event);
        }
    }
}
