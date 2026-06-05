package dao;

import core.Secretary;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface SecretaryDAO {
    void insert(Secretary secretary) throws SQLException, DuplicateEntryException;
    void delete(int secretaryId) throws SQLException, NotFoundException;
    Secretary findById(int secretaryId) throws SQLException, NotFoundException;
    Secretary findByStudentId(int studentId) throws SQLException, NotFoundException;
    List<Secretary> findBySection(int sectionId) throws SQLException;
    List<Secretary> findAll() throws SQLException;
}
