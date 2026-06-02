package user;

import course.Program;
import course.Section;
import course.YearLevel;
import java.util.List;
import java.util.Optional;

public class StudentService {

    private final StudentDAO studentDAO = new StudentDAO();

    public Student createStudent(User user, String studentNumber, String firstName, String middleName, String lastName, Program program, YearLevel yearLevel, Section section){
        Student student = Student.builder()
            .studentId(0)
            .user(user)
            .studentNumber(studentNumber)
            .firstName(firstName)
            .middleName(middleName)
            .lastName(lastName)
            .program(program)
            .yearLevel(yearLevel)
            .section(section)
            .build();
        return studentDAO.save(student);
    }

    public Optional<Student> getStudentById(int id){
        return studentDAO.findById(id);
    }

    public List<Student> getAllStudents(){
        return studentDAO.findAll();
    }

    public Optional<Student> getStudentByNumber(String studentNumber){
        return studentDAO.findByStudentNumber(studentNumber);
    }

    public List<Student> getStudentsBySection(int sectionId){
        return studentDAO.findBySectionId(sectionId);
    }

    public boolean updateStudent(Student student){
        return studentDAO.update(student);
    }

    public boolean deleteStudent(int id){
        return studentDAO.deleteById(id);
    }
}
