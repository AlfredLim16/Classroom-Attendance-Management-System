package dao;

import core.Course;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface CourseDAO {
    void insert(Course course) throws SQLException, DuplicateEntryException;
    void update(Course course) throws SQLException, NotFoundException;
    void delete(int courseId) throws SQLException, NotFoundException;
    Course findById(int courseId) throws SQLException, NotFoundException;
    List<Course> findAll() throws SQLException;
    List<Course> findByProgram(int programId) throws SQLException;
    List<Course> findBySemester(int semesterId) throws SQLException;
}
