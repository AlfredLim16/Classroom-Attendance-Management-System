package dao;

import core.Section;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface SectionDAO {
    void insert(Section section) throws SQLException, DuplicateEntryException;
    void update(Section section) throws SQLException, NotFoundException;
    void delete(int sectionId) throws SQLException, NotFoundException;
    Section findById(int sectionId) throws SQLException, NotFoundException;
    List<Section> findAll() throws SQLException;
    List<Section> findByProgram(int programId) throws SQLException;
}
