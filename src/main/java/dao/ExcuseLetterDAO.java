package dao;

import junction.ExcuseLetter;
import lookup.ExcuseStatus;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface ExcuseLetterDAO {
    void insert(ExcuseLetter letter) throws SQLException, DuplicateEntryException;
    void update(ExcuseLetter letter) throws SQLException, NotFoundException;
    void delete(int excuseId) throws SQLException, NotFoundException;
    ExcuseLetter findById(int excuseId) throws SQLException, NotFoundException;
    List<ExcuseLetter> findByStudent(int studentId) throws SQLException;
    List<ExcuseLetter> findByStudentAndCourse(int studentId, int courseId) throws SQLException;
    List<ExcuseLetter> findByStatus(ExcuseStatus status) throws SQLException;
    List<ExcuseLetter> findAll() throws SQLException;
}
