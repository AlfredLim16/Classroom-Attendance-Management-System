package dao;

import junction.ProfessorSection;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface ProfessorSectionDAO {
    void insert(ProfessorSection professorSection) throws SQLException, DuplicateEntryException;
    void update(ProfessorSection professorSection) throws SQLException, NotFoundException;
    void delete(int professorSectionId) throws SQLException, NotFoundException;
    ProfessorSection findById(int professorSectionId) throws SQLException, NotFoundException;
    List<ProfessorSection> findByProfessor(int professorId) throws SQLException;
    List<ProfessorSection> findBySection(int sectionId) throws SQLException;
    List<ProfessorSection> findAll() throws SQLException;
    /** Persists the isProfessorRecording flag without touching other fields. */
    void updateRecordingFlag(int professorId, int sectionId, boolean isProfessorRecording) throws SQLException, NotFoundException;
    /** Returns the first ProfessorSection matching the given professor+section pair, or empty. */
    java.util.Optional<ProfessorSection> findByProfessorAndSection(int professorId, int sectionId) throws SQLException;
}
