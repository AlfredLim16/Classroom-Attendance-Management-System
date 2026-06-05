package dao;

import core.Professor;
import core.Section;
import core.Semester;
import junction.ProfessorSection;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfessorSectionDAOImpl implements ProfessorSectionDAO {

    private final ProfessorDAO professorDAO;
    private final SectionDAO sectionDAO;
    private final SemesterDAO semesterDAO;

    public ProfessorSectionDAOImpl(ProfessorDAO professorDAO, SectionDAO sectionDAO, SemesterDAO semesterDAO) {
        this.professorDAO = professorDAO;
        this.sectionDAO = sectionDAO;
        this.semesterDAO = semesterDAO;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private ProfessorSection mapRow(ResultSet rs) throws SQLException {
        Professor professor = professorDAO.findById(rs.getInt("professorId"));
        Section section = sectionDAO.findById(rs.getInt("sectionId"));
        Semester semester = semesterDAO.findById(rs.getInt("semesterId"));
        return ProfessorSection.builder()
                .professorSectionId(rs.getInt("professorSectionId"))
                .professor(professor)
                .section(section)
                .semester(semester)
                .isProfessorRecording(rs.getBoolean("isProfessorRecording"))
                .build();
    }

    @Override
    public void insert(ProfessorSection ps2) throws SQLException, DuplicateEntryException {
        String sql = "INSERT INTO ProfessorSection (professorId, sectionId, semesterId, isProfessorRecording) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, ps2.professor().professorId());
            ps.setInt(2, ps2.section().sectionId());
            ps.setInt(3, ps2.semester().semesterId());
            ps.setBoolean(4, ps2.isProfessorRecording());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("ProfessorSection",
                    "professorId+sectionId+semesterId",
                    ps2.professor().professorId() + "+" + ps2.section().sectionId() + "+" + ps2.semester().semesterId());
        }
    }

    @Override
    public void update(ProfessorSection ps2) throws SQLException, NotFoundException {
        String sql = "UPDATE ProfessorSection SET professorId = ?, sectionId = ?, semesterId = ?, isProfessorRecording = ? WHERE professorSectionId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, ps2.professor().professorId());
            ps.setInt(2, ps2.section().sectionId());
            ps.setInt(3, ps2.semester().semesterId());
            ps.setBoolean(4, ps2.isProfessorRecording());
            ps.setInt(5, ps2.professorSectionId());
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("ProfessorSection", ps2.professorSectionId());
            }
        }
    }

    @Override
    public void delete(int professorSectionId) throws SQLException, NotFoundException {
        String sql = "DELETE FROM ProfessorSection WHERE professorSectionId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, professorSectionId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("ProfessorSection", professorSectionId);
            }
        }
    }

    @Override
    public ProfessorSection findById(int professorSectionId) throws SQLException, NotFoundException {
        String sql = "SELECT professorSectionId, professorId, sectionId, semesterId, isProfessorRecording FROM ProfessorSection WHERE professorSectionId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, professorSectionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("ProfessorSection", professorSectionId);
            }
        }
    }

    @Override
    public List<ProfessorSection> findByProfessor(int professorId) throws SQLException {
        String sql = "SELECT professorSectionId, professorId, sectionId, semesterId, isProfessorRecording FROM ProfessorSection WHERE professorId = ?";
        return queryList(sql, professorId);
    }

    @Override
    public List<ProfessorSection> findBySection(int sectionId) throws SQLException {
        String sql = "SELECT professorSectionId, professorId, sectionId, semesterId, isProfessorRecording FROM ProfessorSection WHERE sectionId = ?";
        return queryList(sql, sectionId);
    }

    @Override
    public List<ProfessorSection> findAll() throws SQLException {
        String sql = "SELECT professorSectionId, professorId, sectionId, semesterId, isProfessorRecording FROM ProfessorSection";
        List<ProfessorSection> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private List<ProfessorSection> queryList(String sql, int id) throws SQLException {
        List<ProfessorSection> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }
}
