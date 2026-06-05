package services;

import core.*;
import dao.*;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lookup.YearLevel;

public class StudentService {

    private final StudentDAO studentDAO;
    private final UserDAO userDAO;
    private final ProgramDAO programDAO;
    private final SectionDAO sectionDAO;

    public StudentService(){
        ProgramDAO pDAO = new ProgramDAOImpl();
        SemesterDAO sDAO = new SemesterDAOImpl();
        SectionDAO secDAO = new SectionDAOImpl(pDAO);
        UserDAO uDAO = new UserDAOImpl();
        this.userDAO = uDAO;
        this.programDAO = pDAO;
        this.sectionDAO = secDAO;
        this.studentDAO = new StudentDAOImpl(uDAO, pDAO, secDAO);
    }

    public Student createStudent(User user, String studentNumber, String firstName, String middleName, String lastName, Program program, YearLevel yearLevel, Section section){
        try{
            Student student = Student.builder()
                .user(user).studentNumber(studentNumber)
                .firstName(firstName).middleName(middleName).lastName(lastName)
                .program(program).yearLevel(yearLevel).section(section).build();
            studentDAO.insert(student);
            return studentDAO.findByStudentNumber(studentNumber);
        }catch(SQLException | DuplicateEntryException | NotFoundException e){
            System.err.println("[StudentService] createStudent: " + e.getMessage());
            return null;
        }
    }

    public boolean updateStudent(Student student){
        try{
            studentDAO.update(student);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[StudentService] updateStudent: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteStudent(int studentId){
        try{
            studentDAO.delete(studentId);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[StudentService] deleteStudent: " + e.getMessage());
            return false;
        }
    }

    public Optional<Student> getStudentById(int studentId){
        try{
            return Optional.of(studentDAO.findById(studentId));
        }catch(SQLException | NotFoundException e){
            return Optional.empty();
        }
    }

    public Optional<Student> getStudentByUserId(int userId){
        try{
            return Optional.of(studentDAO.findByUserId(userId));
        }catch(SQLException | NotFoundException e){
            return Optional.empty();
        }
    }

    public List<Student> getAllStudents(){
        try{
            return studentDAO.findAll();
        }catch(SQLException e){
            System.err.println("[StudentService] getAllStudents: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Student> getStudentsBySection(int sectionId){
        try{
            return studentDAO.findBySection(sectionId);
        }catch(SQLException e){
            System.err.println("[StudentService] getStudentsBySection: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
