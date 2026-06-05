package dao;

import core.Semester;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface SemesterDAO {
    void insert(Semester semester) throws SQLException, DuplicateEntryException;
    void update(Semester semester) throws SQLException, NotFoundException;
    void delete(int semesterId) throws SQLException, NotFoundException;
    Semester findById(int semesterId) throws SQLException, NotFoundException;
    List<Semester> findAll() throws SQLException;
}
