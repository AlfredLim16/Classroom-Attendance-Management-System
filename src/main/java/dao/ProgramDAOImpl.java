package dao;

import core.Program;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgramDAOImpl implements ProgramDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Program mapRow(ResultSet rs) throws SQLException {
        return Program.builder()
                .programId(rs.getInt("programId"))
                .programName(rs.getString("programName"))
                .build();
    }

    @Override
    public void insert(Program program) throws SQLException, DuplicateEntryException {
        String sql = "INSERT INTO Program (programName) VALUES (?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, program.programName());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("Program", "programName", program.programName());
        }
    }

    @Override
    public void update(Program program) throws SQLException, NotFoundException {
        String sql = "UPDATE Program SET programName = ? WHERE programId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, program.programName());
            ps.setInt(2, program.programId());
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("Program", program.programId());
            }
        }
    }

    @Override
    public void delete(int programId) throws SQLException, NotFoundException {
        String sql = "DELETE FROM Program WHERE programId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, programId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("Program", programId);
            }
        }
    }

    @Override
    public Program findById(int programId) throws SQLException, NotFoundException {
        String sql = "SELECT programId, programName FROM Program WHERE programId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, programId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("Program", programId);
            }
        }
    }

    @Override
    public List<Program> findAll() throws SQLException {
        String sql = "SELECT programId, programName FROM Program ORDER BY programName";
        List<Program> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }
}
