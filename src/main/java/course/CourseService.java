package course;

import java.util.List;
import java.util.Optional;

public class CourseService {

    private final CourseDAO courseDAO = new CourseDAO();

    public Course createCourse(Program program, String courseCode, String courseName, byte units, Semester semester, YearLevel yearLevel){
        Course course = Course.builder()
            .courseId(0)
            .program(program)
            .courseCode(courseCode)
            .courseName(courseName)
            .units(units)
            .semester(semester)
            .yearLevel(yearLevel)
            .build();
        return courseDAO.save(course);
    }

    public Optional<Course> getCourseById(int id){
        return courseDAO.findById(id);
    }

    public List<Course> getAllCourses(){
        return courseDAO.findAll();
    }

    public List<Course> getCoursesByProgram(int programId){
        return courseDAO.findByProgramId(programId);
    }

    public boolean updateCourse(Course course){
        return courseDAO.update(course);
    }

    public boolean deleteCourse(int id){
        return courseDAO.deleteById(id);
    }
}
