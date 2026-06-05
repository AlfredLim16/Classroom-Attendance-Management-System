package services;

import dao.*;
import junction.ProfessorSection;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ProfessorSectionService {

    private final ProfessorSectionDAO professorSectionDAO;

    public ProfessorSectionService(){
        ProgramDAO programDAO = new ProgramDAOImpl();
        SemesterDAO semesterDAO = new SemesterDAOImpl();
        SectionDAO sectionDAO = new SectionDAOImpl(programDAO);
        UserDAO userDAO = new UserDAOImpl();
        ProfessorDAO professorDAO = new ProfessorDAOImpl(userDAO);
        this.professorSectionDAO = new ProfessorSectionDAOImpl(professorDAO, sectionDAO, semesterDAO);
    }

    public List<ProfessorSection> getSectionsByProfessor(int professorId){
        try{
            return professorSectionDAO.findByProfessor(professorId);
        }catch(SQLException e){
            System.err.println("[ProfessorSectionService] getSectionsByProfessor: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
