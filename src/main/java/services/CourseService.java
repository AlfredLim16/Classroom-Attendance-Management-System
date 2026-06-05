package services;

import core.*;
import dao.*;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import lookup.YearLevel;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class CourseService {

    private final CourseDAO courseDAO;
    private final ProgramDAO programDAO;
    private final SemesterDAO semesterDAO;

    public CourseService(){
        this.programDAO = new ProgramDAOImpl();
        this.semesterDAO = new SemesterDAOImpl();
        this.courseDAO = new CourseDAOImpl(programDAO, semesterDAO);
    }

    public List<Course> getAllCourses(){
        try{
            return courseDAO.findAll();
        }catch(SQLException e){
            System.err.println("[CourseService] getAllCourses: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Course createCourse(Program program, String code, String name,
                                byte units, Semester semester, YearLevel yearLevel){
        try{
            Course course = Course.builder()
                .program(program).courseCode(code).courseName(name)
                .units(units).semester(semester).yearLevel(yearLevel).build();
            courseDAO.insert(course);
            return courseDAO.findAll().stream()
                .filter(c -> c.courseCode().equals(code))
                .findFirst().orElse(null);
        }catch(SQLException | DuplicateEntryException e){
            System.err.println("[CourseService] createCourse: " + e.getMessage());
            return null;
        }
    }

    public boolean updateCourse(Course course){
        try{
            courseDAO.update(course);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[CourseService] updateCourse: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCourse(int courseId){
        try{
            courseDAO.delete(courseId);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[CourseService] deleteCourse: " + e.getMessage());
            return false;
        }
    }
}
