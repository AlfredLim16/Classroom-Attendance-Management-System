package core;

import lookup.YearLevel;
import validations.CourseValidator;
import java.util.Objects;

public record Course(
    int courseId, Program program, String courseCode, String courseName, byte units, Semester semester, YearLevel yearLevel) {

    public Course {
        CourseValidator.validate(program, courseCode, courseName, units, semester, yearLevel);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Course course = (Course) object;
        return courseId == course.courseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }

    @Override
    public String toString() {
        return "Course{id=" + courseId + ", code='" + courseCode + "', name='" + courseName + "'}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int courseId;
        private Program program;
        private String courseCode;
        private String courseName;
        private byte units;
        private Semester semester;
        private YearLevel yearLevel;

        public Builder courseId(int courseId) {
            this.courseId = courseId;
            return this;
        }

        public Builder program(Program program) {
            this.program = program;
            return this;
        }

        public Builder courseCode(String courseCode) {
            this.courseCode = courseCode;
            return this;
        }

        public Builder courseName(String courseName) {
            this.courseName = courseName;
            return this;
        }

        public Builder units(byte units) {
            this.units = units;
            return this;
        }

        public Builder semester(Semester semester) {
            this.semester = semester;
            return this;
        }

        public Builder yearLevel(YearLevel yearLevel) {
            this.yearLevel = yearLevel;
            return this;
        }

        public Course build() {
            return new Course(courseId, program, courseCode, courseName, units, semester, yearLevel);
        }
    }
}
