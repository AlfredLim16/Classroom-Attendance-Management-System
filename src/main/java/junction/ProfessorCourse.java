package junction;

import core.Course;
import core.Professor;
import core.Semester;
import validations.EnrollmentValidator;
import java.util.Objects;

public record ProfessorCourse(int professorCourseId, Professor professor, Course course, Semester semester) {

    public ProfessorCourse {
        EnrollmentValidator.validateProfessorCourse(professor, course, semester);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ProfessorCourse that = (ProfessorCourse) object;
        return professorCourseId == that.professorCourseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(professorCourseId);
    }

    @Override
    public String toString() {
        return "ProfessorCourse{id=" + professorCourseId + ", professor=" + (professor != null ? professor.professorId() : "null") + ", course=" + (course != null ? course.courseCode() : "null") + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int professorCourseId;
        private Professor professor;
        private Course course;
        private Semester semester;

        public Builder professorCourseId(int professorCourseId) {
            this.professorCourseId = professorCourseId;
            return this;
        }

        public Builder professor(Professor professor) {
            this.professor = professor;
            return this;
        }

        public Builder course(Course course) {
            this.course = course;
            return this;
        }

        public Builder semester(Semester semester) {
            this.semester = semester;
            return this;
        }

        public ProfessorCourse build() {
            return new ProfessorCourse(professorCourseId, professor, course, semester);
        }
    }
}
