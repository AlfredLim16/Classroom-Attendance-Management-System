package services;

import dao.*;
import junction.ClassSession;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ClassSessionService {

    private final ClassSessionDAO sessionDAO;

    public ClassSessionService(){
        ProgramDAO programDAO = new ProgramDAOImpl();
        SemesterDAO semesterDAO = new SemesterDAOImpl();
        SchoolEventDAO schoolEventDAO = new SchoolEventDAOImpl();
        SectionDAO sectionDAO = new SectionDAOImpl(programDAO);
        CourseDAO courseDAO = new CourseDAOImpl(programDAO, semesterDAO);
        UserDAO userDAO = new UserDAOImpl();
        ProfessorDAO professorDAO = new ProfessorDAOImpl(userDAO);
        this.sessionDAO = new ClassSessionDAOImpl(courseDAO, sectionDAO, professorDAO, schoolEventDAO);
    }

    public List<ClassSession> getClassSessionsBySection(int sectionId){
        try{
            return sessionDAO.findBySection(sectionId);
        }catch(SQLException e){
            System.err.println("[ClassSessionService] getClassSessionsBySection: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<ClassSession> getClassSessionsByProfessor(int professorId){
        try{
            return sessionDAO.findByProfessor(professorId);
        }catch(SQLException e){
            System.err.println("[ClassSessionService] getClassSessionsByProfessor: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<ClassSession> getAllClassSessions(){
        try{
            return sessionDAO.findAll();
        }catch(SQLException e){
            System.err.println("[ClassSessionService] getAllClassSessions: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
