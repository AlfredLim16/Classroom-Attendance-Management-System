package services;

import dao.*;
import exceptions.NotFoundException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import core.Professor;
import core.Section;
import core.Semester;
import junction.ProfessorSection;

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

    public boolean assignSection(Professor professor, Section section, Semester semester){
        try{
            ProfessorSection ps = ProfessorSection.builder()
                .professor(professor)
                .section(section)
                .semester(semester)
                .isProfessorRecording(false)
                .build();
            professorSectionDAO.insert(ps);
            return true;
        }catch(SQLException | exceptions.DuplicateEntryException e){
            System.err.println("[ProfessorSectionService] assignSection: " + e.getMessage());
            return false;
        }
    }

    public boolean unassignSection(int professorSectionId){
        try{
            professorSectionDAO.delete(professorSectionId);
            return true;
        }catch(SQLException | exceptions.NotFoundException e){
            System.err.println("[ProfessorSectionService] unassignSection: " + e.getMessage());
            return false;
        }
    }

    public boolean updateRecordingFlag(int professorId, int sectionId, boolean isProfessorRecording){
        try{
            professorSectionDAO.updateRecordingFlag(professorId, sectionId, isProfessorRecording);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[ProfessorSectionService] updateRecordingFlag: " + e.getMessage());
            return false;
        }
    }

    public boolean getRecordingFlag(int professorId, int sectionId){
        try{
            Optional<ProfessorSection> opt = professorSectionDAO.findByProfessorAndSection(professorId, sectionId);
            return opt.map(ps -> !ps.isProfessorRecording()).orElse(false);
        }catch(SQLException e){
            System.err.println("[ProfessorSectionService] getRecordingFlag: " + e.getMessage());
            return false;
        }
    }

    public boolean getRecordingFlagForSection(int sectionId){
        try{
            List<ProfessorSection> rows = professorSectionDAO.findBySection(sectionId);
            if(rows.isEmpty()) return false;
            return rows.stream().anyMatch(ps -> !ps.isProfessorRecording());
        }catch(SQLException e){
            System.err.println("[ProfessorSectionService] getRecordingFlagForSection: " + e.getMessage());
            return false;
        }
    }
}
