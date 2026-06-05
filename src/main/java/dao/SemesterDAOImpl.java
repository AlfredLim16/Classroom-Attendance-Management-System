package dao;

import core.Semester;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SemesterDAOImpl implements SemesterDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Semester mapRow(ResultSet rs) throws SQLException {
        return new Semester(
                rs.getInt("semesterId"),
                rs.getString("semesterName"),
                rs.getString("schoolYear"),
                rs.getDate("startDate").toLocalDate(),
                rs.getDate("endDate").toLocalDate()
        );
    }

    @Override
    public void insert(Semester semester) throws SQLException, DuplicateEntryException {
        String sql = "INSERT INTO Semester (semesterName, schoolYear, startDate, endDate) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, semester.semesterName());
            ps.setString(2, semester.schoolYear());
            ps.setDate(3, Date.valueOf(semester.startDate()));
            ps.setDate(4, Date.valueOf(semester.endDate()));
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("Semester", "semesterName+schoolYear",
                    semester.semesterName() + " " + semester.schoolYear());
        }
    }

    @Override
    public void update(Semester semester) throws SQLException, NotFoundException {
        String sql = "UPDATE Semester SET semesterName = ?, schoolYear = ?, startDate = ?, endDate = ? WHERE semesterId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, semester.semesterName());
            ps.setString(2, semester.schoolYear());
            ps.setDate(3, Date.valueOf(semester.startDate()));
            ps.setDate(4, Date.valueOf(semester.endDate()));
            ps.setInt(5, semester.semesterId());
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("Semester", semester.semesterId());
            }
        }
    }

    @Override
    public void delete(int semesterId) throws SQLException, NotFoundException {
        String sql = "DELETE FROM Semester WHERE semesterId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, semesterId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("Semester", semesterId);
            }
        }
    }

    @Override
    public Semester findById(int semesterId) throws SQLException, NotFoundException {
        String sql = "SELECT semesterId, semesterName, schoolYear, startDate, endDate FROM Semester WHERE semesterId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, semesterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("Semester", semesterId);
            }
        }
    }

    @Override
    public List<Semester> findAll() throws SQLException {
        String sql = "SELECT semesterId, semesterName, schoolYear, startDate, endDate FROM Semester ORDER BY startDate DESC";
        List<Semester> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }
}
