package dao;

import core.Professor;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface ProfessorDAO {
    void insert(Professor professor) throws SQLException, DuplicateEntryException;
    void update(Professor professor) throws SQLException, NotFoundException;
    void delete(int professorId) throws SQLException, NotFoundException;
    Professor findById(int professorId) throws SQLException, NotFoundException;
    Professor findByUserId(int userId) throws SQLException, NotFoundException;
    List<Professor> findAll() throws SQLException;
}
