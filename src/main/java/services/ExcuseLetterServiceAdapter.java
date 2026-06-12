package services;

import core.Course;
import core.Student;
import core.User;
import dao.*;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import junction.ExcuseLetter;
import lookup.ExcuseStatus;

public class ExcuseLetterServiceAdapter {

    private final ExcuseLetterDAO excuseLetterDAO;
    private final ExcuseLetterService excuseLetterService;

    public ExcuseLetterServiceAdapter(){
        ProgramDAO programDAO = new ProgramDAOImpl();
        SemesterDAO semesterDAO = new SemesterDAOImpl();
        SectionDAO sectionDAO = new SectionDAOImpl(programDAO);
        CourseDAO courseDAO = new CourseDAOImpl(programDAO, semesterDAO);
        UserDAO userDAO = new UserDAOImpl();
        StudentDAO studentDAO = new StudentDAOImpl(userDAO, programDAO, sectionDAO);
        this.excuseLetterDAO = new ExcuseLetterDAOImpl(studentDAO, courseDAO, userDAO);
        this.excuseLetterService = new ExcuseLetterService(excuseLetterDAO);
    }

    public ExcuseLetter submitExcuseLetter(Student student, Course course, LocalDate absentDate,
        String reason, String documentPath,
        ExcuseStatus status, LocalDateTime submittedDate){
        try{
            ExcuseLetter letter = ExcuseLetter.builder()
                .student(student).course(course).absentDate(absentDate)
                .reason(reason).supportingDocumentPath(documentPath)
                .status(ExcuseStatus.PENDING).reviewedBy(null)
                .submittedDate(submittedDate != null ? submittedDate : LocalDateTime.now())
                .reviewedDate(null).build();
            excuseLetterDAO.insert(letter);
            return letter;
        }catch(SQLException | DuplicateEntryException e){
            System.err.println("[ExcuseLetterServiceAdapter] submitExcuseLetter: " + e.getMessage());
            return null;
        }
    }

    public boolean reviewExcuseLetter(int excuseId, User reviewedBy,
        ExcuseStatus status, LocalDateTime reviewedDate){
        try{
            excuseLetterService.reviewExcuseLetter(excuseId, status, reviewedBy, reviewedDate != null ? reviewedDate : LocalDateTime.now());
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[ExcuseLetterServiceAdapter] reviewExcuseLetter: " + e.getMessage());
            return false;
        }
    }

    public List<ExcuseLetter> getExcuseLettersByStudent(int studentId){
        try{
            return excuseLetterDAO.findByStudent(studentId);
        }catch(SQLException e){
            System.err.println("[ExcuseLetterServiceAdapter] getExcuseLettersByStudent: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<ExcuseLetter> getAllExcuseLetters(){
        try{
            return excuseLetterDAO.findAll();
        }catch(SQLException e){
            System.err.println("[ExcuseLetterServiceAdapter] getAllExcuseLetters: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
