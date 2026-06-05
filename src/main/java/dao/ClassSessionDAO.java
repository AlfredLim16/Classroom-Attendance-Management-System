package dao;

import junction.ClassSession;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface ClassSessionDAO {
    void insert(ClassSession session) throws SQLException, DuplicateEntryException;
    void update(ClassSession session) throws SQLException, NotFoundException;
    void delete(int sessionId) throws SQLException, NotFoundException;
    ClassSession findById(int sessionId) throws SQLException, NotFoundException;
    List<ClassSession> findByCourse(int courseId) throws SQLException;
    List<ClassSession> findBySection(int sectionId) throws SQLException;
    List<ClassSession> findByProfessor(int professorId) throws SQLException;
    List<ClassSession> findByDate(LocalDate date) throws SQLException;
    /** All sessions in a date range — used for week/month/semester filters. */
    List<ClassSession> findByDateRange(LocalDate from, LocalDate to) throws SQLException;
    List<ClassSession> findAll() throws SQLException;
}
