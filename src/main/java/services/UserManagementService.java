package services;

import core.*;
import dao.*;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import java.sql.SQLException;
import java.util.List;
import lookup.*;
import validations.UserValidator;

public class UserManagementService {

    private final UserDAO userDAO;
    private final StudentDAO studentDAO;
    private final ProfessorDAO professorDAO;
    private final SecretaryDAO secretaryDAO;
    private final ProgramDAO programDAO;
    private final SectionDAO sectionDAO;

    public UserManagementService(){
        ProgramDAO pDAO = new ProgramDAOImpl();
        SemesterDAO semesterDAO = new SemesterDAOImpl();
        SectionDAO secDAO = new SectionDAOImpl(pDAO);
        UserDAO uDAO = new UserDAOImpl();
        this.programDAO = pDAO;
        this.sectionDAO = secDAO;
        this.userDAO = uDAO;
        this.studentDAO = new StudentDAOImpl(uDAO, pDAO, secDAO);
        this.professorDAO = new ProfessorDAOImpl(uDAO);
        this.secretaryDAO = new SecretaryDAOImpl(studentDAO, secDAO);
    }

    public void createUser(String username, String rawPassword, Role role)
        throws SQLException, DuplicateEntryException{
        UserValidator.validateRawPassword(rawPassword);
        String hashedPassword = PasswordUtil.hash(rawPassword);
        User user = User.builder()
            .userName(username)
            .userPassword(hashedPassword)
            .role(role)
            .build();
        userDAO.insert(user);
    }

    public void updateUser(User user) throws SQLException, NotFoundException{
        userDAO.update(user);
    }

    public void deleteUser(int userId) throws SQLException, NotFoundException{
        userDAO.delete(userId);
    }

    public User getUserById(int userId) throws SQLException, NotFoundException{
        return userDAO.findById(userId);
    }

    public List<User> getAllUsers() throws SQLException{
        return userDAO.findAll();
    }

    public void createStudent(User user, String studentNumber, String firstName,
        String middleName, String lastName,
        Program program, YearLevel yearLevel, Section section)
        throws SQLException, DuplicateEntryException{
        Student student = Student.builder()
            .user(user)
            .studentNumber(studentNumber)
            .firstName(firstName)
            .middleName(middleName)
            .lastName(lastName)
            .program(program)
            .yearLevel(yearLevel)
            .section(section)
            .build();
        studentDAO.insert(student);
    }

    public void updateStudent(Student student) throws SQLException, NotFoundException{
        studentDAO.update(student);
    }

    public void deleteStudent(int studentId) throws SQLException, NotFoundException{
        studentDAO.delete(studentId);
    }

    public Student getStudentById(int studentId) throws SQLException, NotFoundException{
        return studentDAO.findById(studentId);
    }

    public List<Student> getAllStudents() throws SQLException{
        return studentDAO.findAll();
    }

    public List<Student> getStudentsBySection(int sectionId) throws SQLException{
        return studentDAO.findBySection(sectionId);
    }

    public void createProfessor(User user, String firstName, String middleName,
        String lastName, ProfessorType type)
        throws SQLException, DuplicateEntryException{
        Professor professor = Professor.builder()
            .user(user)
            .firstName(firstName)
            .middleName(middleName)
            .lastName(lastName)
            .professorType(type)
            .build();
        professorDAO.insert(professor);
    }

    public void updateProfessor(Professor professor) throws SQLException, NotFoundException{
        professorDAO.update(professor);
    }

    public void deleteProfessor(int professorId) throws SQLException, NotFoundException{
        professorDAO.delete(professorId);
    }

    public List<Professor> getAllProfessors() throws SQLException{
        return professorDAO.findAll();
    }

    public void assignSecretary(Student student, Section section)
        throws SQLException, DuplicateEntryException{
        Secretary secretary = Secretary.builder()
            .student(student)
            .section(section)
            .build();
        secretaryDAO.insert(secretary);
    }

    public void removeSecretary(int secretaryId) throws SQLException, NotFoundException{
        secretaryDAO.delete(secretaryId);
    }

    public List<Secretary> getAllSecretaries() throws SQLException{
        return secretaryDAO.findAll();
    }

    public List<Program> getAllPrograms() throws SQLException{
        return programDAO.findAll();
    }

    public List<Section> getAllSections() throws SQLException{
        return sectionDAO.findAll();
    }

    public List<Section> getSectionsByProgram(int programId) throws SQLException{
        return sectionDAO.findByProgram(programId);
    }
}
