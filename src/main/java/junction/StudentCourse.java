package junction;

import core.Course;
import core.Semester;
import core.Student;
import validations.EnrollmentValidator;
import java.util.Objects;

public record StudentCourse(int studentCourseId, Student student, Course course, Semester semester) {

    public StudentCourse {
        EnrollmentValidator.validateStudentCourse(student, course, semester);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        StudentCourse that = (StudentCourse) object;
        return studentCourseId == that.studentCourseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentCourseId);
    }

    @Override
    public String toString() {
        return "StudentCourse{id=" + studentCourseId + ", student=" + (student != null ? student.studentNumber() : "null") + ", course=" + (course != null ? course.courseCode() : "null") + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int studentCourseId;
        private Student student;
        private Course course;
        private Semester semester;

        public Builder studentCourseId(int studentCourseId) {
            this.studentCourseId = studentCourseId;
            return this;
        }

        public Builder student(Student student) {
            this.student = student;
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

        public StudentCourse build() {
            return new StudentCourse(studentCourseId, student, course, semester);
        }
    }
}
