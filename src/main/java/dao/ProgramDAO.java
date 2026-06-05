package dao;

import core.Program;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface ProgramDAO {
    void insert(Program program) throws SQLException, DuplicateEntryException;
    void update(Program program) throws SQLException, NotFoundException;
    void delete(int programId) throws SQLException, NotFoundException;
    Program findById(int programId) throws SQLException, NotFoundException;
    List<Program> findAll() throws SQLException;
}
