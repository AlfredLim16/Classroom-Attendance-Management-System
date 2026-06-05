package dao;

import junction.StudentCourse;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface StudentCourseDAO {
    void insert(StudentCourse studentCourse) throws SQLException, DuplicateEntryException;
    void delete(int studentCourseId) throws SQLException, NotFoundException;
    StudentCourse findById(int studentCourseId) throws SQLException, NotFoundException;
    List<StudentCourse> findByStudent(int studentId) throws SQLException;
    List<StudentCourse> findByCourse(int courseId) throws SQLException;
    List<StudentCourse> findBySemester(int semesterId) throws SQLException;
    List<StudentCourse> findAll() throws SQLException;
}
