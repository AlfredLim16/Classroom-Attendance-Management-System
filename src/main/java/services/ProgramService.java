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
}
