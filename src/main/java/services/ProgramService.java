package services;

import core.Program;
import dao.*;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ProgramService {

    private final ProgramDAO programDAO;

    public ProgramService(){
        this.programDAO = new ProgramDAOImpl();
    }

    public List<Program> getAllPrograms(){
        try{
            return programDAO.findAll();
        }catch(SQLException e){
            System.err.println("[ProgramService] getAllPrograms: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Program createProgram(String programName){
        try{
            Program program = new Program(0, programName);
            programDAO.insert(program);
            List<Program> all = programDAO.findAll();
            return all.stream()
                .filter(p -> p.programName().equals(programName))
                .findFirst().orElse(null);
        }catch(SQLException | exceptions.DuplicateEntryException e){
            System.err.println("[ProgramService] createProgram: " + e.getMessage());
            return null;
        }
    }

    public boolean updateProgram(Program program){
        try{
            programDAO.update(program);
            return true;
        }catch(SQLException | exceptions.NotFoundException e){
            System.err.println("[ProgramService] updateProgram: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProgram(int programId){
        try{
            programDAO.delete(programId);
            return true;
        }catch(SQLException | exceptions.NotFoundException e){
            System.err.println("[ProgramService] deleteProgram: " + e.getMessage());
            return false;
        }
    }
}
