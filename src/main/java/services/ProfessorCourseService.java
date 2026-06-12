package services;

import dao.*;
import core.Professor;
import core.Course;
import core.Semester;
import junction.ProfessorCourse;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ProfessorCourseService {

    private final ProfessorCourseDAO professorCourseDAO;

    public ProfessorCourseService(){
        ProgramDAO programDAO = new ProgramDAOImpl();
        SemesterDAO semesterDAO = new SemesterDAOImpl();
        SectionDAO sectionDAO = new SectionDAOImpl(programDAO);
        CourseDAO courseDAO = new CourseDAOImpl(programDAO, semesterDAO);
        UserDAO userDAO = new UserDAOImpl();
        ProfessorDAO professorDAO = new ProfessorDAOImpl(userDAO);
        this.professorCourseDAO = new ProfessorCourseDAOImpl(professorDAO, courseDAO, semesterDAO);
    }

    public List<ProfessorCourse> getCoursesByProfessor(int professorId){
        try{
            return professorCourseDAO.findByProfessor(professorId);
        }catch(SQLException e){
            System.err.println("[ProfessorCourseService] getCoursesByProfessor: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean assignCourse(Professor professor, Course course, Semester semester){
        try{
            ProfessorCourse pc = ProfessorCourse.builder()
                .professor(professor)
                .course(course)
                .semester(semester)
                .build();
            professorCourseDAO.insert(pc);
            return true;
        }catch(SQLException | exceptions.DuplicateEntryException e){
            System.err.println("[ProfessorCourseService] assignCourse: " + e.getMessage());
            return false;
        }
    }

    public boolean unassignCourse(int professorCourseId){
        try{
            professorCourseDAO.delete(professorCourseId);
            return true;
        }catch(SQLException | exceptions.NotFoundException e){
            System.err.println("[ProfessorCourseService] unassignCourse: " + e.getMessage());
            return false;
        }
    }
}
