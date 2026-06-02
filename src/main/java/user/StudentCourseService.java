package user;

import course.Course;
import course.Semester;
import java.util.List;
import java.util.Optional;

public class StudentCourseService {

    private final StudentCourseDAO studentCourseDAO = new StudentCourseDAO();

    public StudentCourse enrollStudentToCourse(Student student, Course course, Semester semester){
        StudentCourse sc = new StudentCourse(0, student, course, semester);
        return studentCourseDAO.save(sc);
    }

    public Optional<StudentCourse> getStudentCourseById(int id){
        return studentCourseDAO.findById(id);
    }

    public List<StudentCourse> getAllStudentCourses(){
        return studentCourseDAO.findAll();
    }

    public List<StudentCourse> getCoursesByStudent(int studentId){
        return studentCourseDAO.findByStudentId(studentId);
    }

    public List<StudentCourse> getStudentsByCourse(int courseId){
        return studentCourseDAO.findByCourseId(courseId);
    }

    public boolean updateStudentCourse(StudentCourse sc){
        return studentCourseDAO.update(sc);
    }

    public boolean deleteStudentCourse(int id){
        return studentCourseDAO.deleteById(id);
    }
}
