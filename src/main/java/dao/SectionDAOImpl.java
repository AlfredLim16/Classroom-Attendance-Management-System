package dao;

import core.Program;
import core.Section;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import lookup.YearLevel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDAOImpl implements SectionDAO {

    private final ProgramDAO programDAO;

    public SectionDAOImpl(ProgramDAO programDAO) {
        this.programDAO = programDAO;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Section mapRow(ResultSet rs) throws SQLException {
        try {
            Program program = programDAO.findById(rs.getInt("programId"));
            YearLevel yearLevel = YearLevel.fromId(rs.getInt("yearLevelId"));
            return Section.builder()
                    .sectionId(rs.getInt("sectionId"))
                    .program(program)
                    .yearLevel(yearLevel)
                    .sectionCode(rs.getString("sectionCode"))
                    .build();
        } catch (ValidationException e) {
            throw new SQLException("Invalid yearLevelId in database for sectionId=" + rs.getInt("sectionId") + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void insert(Section section) throws SQLException, DuplicateEntryException {
        String sql = "INSERT INTO Section (programId, yearLevelId, sectionCode) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, section.program().programId());
            ps.setInt(2, section.yearLevel().getYearLevelId());
            ps.setString(3, section.sectionCode());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("Section", "sectionCode", section.sectionCode());
        }
    }

    @Override
    public void update(Section section) throws SQLException, NotFoundException {
        String sql = "UPDATE Section SET programId = ?, yearLevelId = ?, sectionCode = ? WHERE sectionId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, section.program().programId());
            ps.setInt(2, section.yearLevel().getYearLevelId());
            ps.setString(3, section.sectionCode());
            ps.setInt(4, section.sectionId());
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("Section", section.sectionId());
            }
        }
    }

    @Override
    public void delete(int sectionId) throws SQLException, NotFoundException {
        String sql = "DELETE FROM Section WHERE sectionId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("Section", sectionId);
            }
        }
    }

    @Override
    public Section findById(int sectionId) throws SQLException, NotFoundException {
        String sql = "SELECT sectionId, programId, yearLevelId, sectionCode FROM Section WHERE sectionId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("Section", sectionId);
            }
        }
    }

    @Override
    public List<Section> findAll() throws SQLException {
        String sql = "SELECT sectionId, programId, yearLevelId, sectionCode FROM Section ORDER BY sectionCode";
        List<Section> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Section> findByProgram(int programId) throws SQLException {
        String sql = "SELECT sectionId, programId, yearLevelId, sectionCode FROM Section WHERE programId = ? ORDER BY sectionCode";
        List<Section> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, programId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }
}
