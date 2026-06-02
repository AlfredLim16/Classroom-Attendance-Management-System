package user;

import course.Course;
import course.Semester;

public record ProfessorCourse(int professorCourseId, Professor professor, Course course, Semester semester) {

}
