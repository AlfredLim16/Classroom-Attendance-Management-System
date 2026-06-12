package services;

import core.Course;
import core.Semester;
import core.Student;
import dao.*;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import junction.StudentCourse;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class StudentCourseService {

    private final StudentCourseDAO studentCourseDAO;

    public StudentCourseService(){
        ProgramDAO programDAO = new ProgramDAOImpl();
        SemesterDAO semesterDAO = new SemesterDAOImpl();
        SectionDAO sectionDAO = new SectionDAOImpl(programDAO);
        CourseDAO courseDAO = new CourseDAOImpl(programDAO, semesterDAO);
        UserDAO userDAO = new UserDAOImpl();
        StudentDAO studentDAO = new StudentDAOImpl(userDAO, programDAO, sectionDAO);
        this.studentCourseDAO = new StudentCourseDAOImpl(studentDAO, courseDAO, semesterDAO);
    }

    public List<StudentCourse> getCoursesByStudent(int studentId){
        try{
            return studentCourseDAO.findByStudent(studentId);
        }catch(SQLException e){
            System.err.println("[StudentCourseService] getCoursesByStudent: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean enrollStudent(Student student, Course course, Semester semester){
        try{
            StudentCourse sc = StudentCourse.builder()
                .student(student)
                .course(course)
                .semester(semester)
                .build();
            studentCourseDAO.insert(sc);
            return true;
        }catch(SQLException | DuplicateEntryException e){
            System.err.println("[StudentCourseService] enrollStudent: " + e.getMessage());
            return false;
        }
    }

    public boolean unenrollStudent(int studentCourseId){
        try{
            studentCourseDAO.delete(studentCourseId);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[StudentCourseService] unenrollStudent: " + e.getMessage());
            return false;
        }
    }
}
