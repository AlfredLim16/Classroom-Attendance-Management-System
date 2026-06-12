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

    public boolean createSemester(String name, String schoolYear, java.time.LocalDate startDate, java.time.LocalDate endDate){
        try{
            Semester semester = new Semester(0, name, schoolYear, startDate, endDate);
            semesterDAO.insert(semester);
            return true;
        }catch(SQLException | exceptions.DuplicateEntryException e){
            System.err.println("[SemesterService] createSemester: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSemester(int semesterId){
        try{
            semesterDAO.delete(semesterId);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[SemesterService] deleteSemester: " + e.getMessage());
            return false;
        }
    }
}
