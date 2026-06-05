package dao;

import core.Course;
import junction.AttendancePolicy;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendancePolicyDAOImpl implements AttendancePolicyDAO {

    private final CourseDAO courseDAO;

    public AttendancePolicyDAOImpl(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private AttendancePolicy mapRow(ResultSet rs) throws SQLException {
        Course course = courseDAO.findById(rs.getInt("courseId"));
        return AttendancePolicy.builder()
                .policyId(rs.getInt("policyId"))
                .course(course)
                .lateThresholdMinutes(rs.getInt("lateThresholdMinutes"))
                .latesEqualToAbsent(rs.getInt("latesEqualToAbsent"))
                .absentsEqualToDropped(rs.getInt("absentsEqualToDropped"))
                .isActive(rs.getBoolean("isActive"))
                .build();
    }

    @Override
    public void insert(AttendancePolicy policy) throws SQLException, DuplicateEntryException {
        String sql = "INSERT INTO AttendancePolicy (courseId, lateThresholdMinutes, latesEqualToAbsent, absentsEqualToDropped, isActive) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, policy.course().courseId());
            ps.setInt(2, policy.lateThresholdMinutes());
            ps.setInt(3, policy.latesEqualToAbsent());
            ps.setInt(4, policy.absentsEqualToDropped());
            ps.setBoolean(5, policy.isActive());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("AttendancePolicy", "courseId", policy.course().courseId());
        }
    }

    @Override
    public void update(AttendancePolicy policy) throws SQLException, NotFoundException {
        String sql = "UPDATE AttendancePolicy SET courseId = ?, lateThresholdMinutes = ?, latesEqualToAbsent = ?, absentsEqualToDropped = ?, isActive = ? WHERE policyId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, policy.course().courseId());
            ps.setInt(2, policy.lateThresholdMinutes());
            ps.setInt(3, policy.latesEqualToAbsent());
            ps.setInt(4, policy.absentsEqualToDropped());
            ps.setBoolean(5, policy.isActive());
            ps.setInt(6, policy.policyId());
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("AttendancePolicy", policy.policyId());
            }
        }
    }

    @Override
    public void delete(int policyId) throws SQLException, NotFoundException {
        String sql = "DELETE FROM AttendancePolicy WHERE policyId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, policyId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("AttendancePolicy", policyId);
            }
        }
    }

    @Override
    public AttendancePolicy findById(int policyId) throws SQLException, NotFoundException {
        String sql = "SELECT policyId, courseId, lateThresholdMinutes, latesEqualToAbsent, absentsEqualToDropped, isActive FROM AttendancePolicy WHERE policyId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, policyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("AttendancePolicy", policyId);
            }
        }
    }

    @Override
    public AttendancePolicy findActiveByCourse(int courseId) throws SQLException, NotFoundException {
        String sql = "SELECT policyId, courseId, lateThresholdMinutes, latesEqualToAbsent, absentsEqualToDropped, isActive FROM AttendancePolicy WHERE courseId = ? AND isActive = TRUE LIMIT 1";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("AttendancePolicy", "active policy for courseId=" + courseId);
            }
        }
    }

    @Override
    public List<AttendancePolicy> findByCourse(int courseId) throws SQLException {
        String sql = "SELECT policyId, courseId, lateThresholdMinutes, latesEqualToAbsent, absentsEqualToDropped, isActive FROM AttendancePolicy WHERE courseId = ?";
        List<AttendancePolicy> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<AttendancePolicy> findAll() throws SQLException {
        String sql = "SELECT policyId, courseId, lateThresholdMinutes, latesEqualToAbsent, absentsEqualToDropped, isActive FROM AttendancePolicy";
        List<AttendancePolicy> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }
}
