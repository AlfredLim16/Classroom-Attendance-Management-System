package dao;

import core.Student;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface StudentDAO {
    void insert(Student student) throws SQLException, DuplicateEntryException;
    void update(Student student) throws SQLException, NotFoundException;
    void delete(int studentId) throws SQLException, NotFoundException;
    Student findById(int studentId) throws SQLException, NotFoundException;
    Student findByStudentNumber(String studentNumber) throws SQLException, NotFoundException;
    Student findByUserId(int userId) throws SQLException, NotFoundException;
    List<Student> findAll() throws SQLException;
    List<Student> findBySection(int sectionId) throws SQLException;
}
