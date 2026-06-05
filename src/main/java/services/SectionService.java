package services;

import core.*;
import dao.*;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import lookup.YearLevel;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class SectionService {

    private final SectionDAO sectionDAO;
    private final ProgramDAO programDAO;

    public SectionService(){
        this.programDAO = new ProgramDAOImpl();
        this.sectionDAO = new SectionDAOImpl(programDAO);
    }

    public List<Section> getAllSections(){
        try{
            return sectionDAO.findAll();
        }catch(SQLException e){
            System.err.println("[SectionService] getAllSections: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Section createSection(Program program, YearLevel yearLevel, String sectionCode){
        try{
            Section section = Section.builder()
                .program(program).yearLevel(yearLevel).sectionCode(sectionCode).build();
            sectionDAO.insert(section);
            return sectionDAO.findAll().stream()
                .filter(s -> s.sectionCode().equals(sectionCode))
                .findFirst().orElse(null);
        }catch(SQLException | DuplicateEntryException e){
            System.err.println("[SectionService] createSection: " + e.getMessage());
            return null;
        }
    }

    public boolean updateSection(Section section){
        try{
            sectionDAO.update(section);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[SectionService] updateSection: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSection(int sectionId){
        try{
            sectionDAO.delete(sectionId);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[SectionService] deleteSection: " + e.getMessage());
            return false;
        }
    }
}
