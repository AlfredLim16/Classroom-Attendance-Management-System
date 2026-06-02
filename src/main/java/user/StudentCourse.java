package user;

import course.Course;
import course.Semester;

public record StudentCourse(int studentCourseId, Student student, Course course, Semester semester) {
}
