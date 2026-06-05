package services;

import core.*;
import dao.*;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SecretaryService {

    private final SecretaryDAO secretaryDAO;
    private final StudentDAO studentDAO;

    public SecretaryService(){
        ProgramDAO pDAO = new ProgramDAOImpl();
        SemesterDAO sDAO = new SemesterDAOImpl();
        SectionDAO secDAO = new SectionDAOImpl(pDAO);
        UserDAO uDAO = new UserDAOImpl();
        this.studentDAO = new StudentDAOImpl(uDAO, pDAO, secDAO);
        this.secretaryDAO = new SecretaryDAOImpl(studentDAO, secDAO);
    }

    public Optional<Secretary> getSecretaryByStudentNumber(String studentNumber){
        try{
            Student student = studentDAO.findByStudentNumber(studentNumber);
            return Optional.of(secretaryDAO.findByStudentId(student.studentId()));
        }catch(SQLException | NotFoundException e){
            return Optional.empty();
        }
    }

    public Optional<Secretary> getSecretaryByUserId(int userId){
        try{
            Student student = studentDAO.findByUserId(userId);
            return Optional.of(secretaryDAO.findByStudentId(student.studentId()));
        }catch(SQLException | NotFoundException e){
            return Optional.empty();
        }
    }

    public List<Secretary> getAllSecretaries(){
        try{
            return secretaryDAO.findAll();
        }catch(SQLException e){
            System.err.println("[SecretaryService] getAllSecretaries: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
