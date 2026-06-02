package user;

import course.Course;
import course.Semester;
import java.util.List;
import java.util.Optional;

public class ProfessorCourseService {

    private final ProfessorCourseDAO professorCourseDAO = new ProfessorCourseDAO();

    public ProfessorCourse assignProfessorToCourse(Professor professor, Course course, Semester semester){
        ProfessorCourse pc = new ProfessorCourse(0, professor, course, semester);
        return professorCourseDAO.save(pc);
    }

    public Optional<ProfessorCourse> getProfessorCourseById(int id){
        return professorCourseDAO.findById(id);
    }

    public List<ProfessorCourse> getAllProfessorCourses(){
        return professorCourseDAO.findAll();
    }

    public List<ProfessorCourse> getCoursesByProfessor(int professorId){
        return professorCourseDAO.findByProfessorId(professorId);
    }

    public List<ProfessorCourse> getProfessorsByCourse(int courseId){
        return professorCourseDAO.findByCourseId(courseId);
    }

    public boolean updateProfessorCourse(ProfessorCourse pc){
        return professorCourseDAO.update(pc);
    }

    public boolean deleteProfessorCourse(int id){
        return professorCourseDAO.deleteById(id);
    }
}
