package dao;

import core.Student;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class SecretaryStudentDAO {

    private final StudentDAO studentDAO;

    public SecretaryStudentDAO(){
        ProgramDAO programDAO = new ProgramDAOImpl();
        SemesterDAO semesterDAO = new SemesterDAOImpl();
        SectionDAO sectionDAO = new SectionDAOImpl(programDAO);
        UserDAO userDAO = new UserDAOImpl();
        this.studentDAO = new StudentDAOImpl(userDAO, programDAO, sectionDAO);
    }

    public List<Student> findStudentsBySectionId(int sectionId){
        try{
            return studentDAO.findBySection(sectionId);
        }catch(SQLException e){
            System.err.println("[SecretaryStudentDAO] findStudentsBySectionId: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
