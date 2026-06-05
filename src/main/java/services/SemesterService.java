package services;

import core.Semester;
import dao.*;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class SemesterService {

    private final SemesterDAO semesterDAO;

    public SemesterService(){
        this.semesterDAO = new SemesterDAOImpl();
    }

    public List<Semester> getAllSemesters(){
        try{
            return semesterDAO.findAll();
        }catch(SQLException e){
            System.err.println("[SemesterService] getAllSemesters: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean updateSemester(Semester semester){
        try{
            semesterDAO.update(semester);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[SemesterService] updateSemester: " + e.getMessage());
            return false;
        }
    }
}
