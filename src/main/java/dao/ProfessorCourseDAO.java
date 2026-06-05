package dao;

import junction.ProfessorCourse;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface ProfessorCourseDAO {
    void insert(ProfessorCourse professorCourse) throws SQLException, DuplicateEntryException;
    void delete(int professorCourseId) throws SQLException, NotFoundException;
    ProfessorCourse findById(int professorCourseId) throws SQLException, NotFoundException;
    List<ProfessorCourse> findByProfessor(int professorId) throws SQLException;
    List<ProfessorCourse> findByCourse(int courseId) throws SQLException;
    List<ProfessorCourse> findBySemester(int semesterId) throws SQLException;
    List<ProfessorCourse> findAll() throws SQLException;
}
