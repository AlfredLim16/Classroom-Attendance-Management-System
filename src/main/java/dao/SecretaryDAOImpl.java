package dao;

import core.Secretary;
import core.Section;
import core.Student;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SecretaryDAOImpl implements SecretaryDAO {

    private final StudentDAO studentDAO;
    private final SectionDAO sectionDAO;

    public SecretaryDAOImpl(StudentDAO studentDAO, SectionDAO sectionDAO) {
        this.studentDAO = studentDAO;
        this.sectionDAO = sectionDAO;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Secretary mapRow(ResultSet rs) throws SQLException {
        Student student = studentDAO.findById(rs.getInt("studentId"));
        Section section = sectionDAO.findById(rs.getInt("sectionId"));
        return Secretary.builder()
                .secretaryId(rs.getInt("secretaryId"))
                .student(student)
                .section(section)
                .build();
    }

    @Override
    public void insert(Secretary secretary) throws SQLException, DuplicateEntryException {
        String sql = "INSERT INTO Secretary (studentId, sectionId) VALUES (?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, secretary.student().studentId());
            ps.setInt(2, secretary.section().sectionId());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("Secretary", "studentId", secretary.student().studentId());
        }
    }

    @Override
    public void delete(int secretaryId) throws SQLException, NotFoundException {
        String sql = "DELETE FROM Secretary WHERE secretaryId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, secretaryId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("Secretary", secretaryId);
            }
        }
    }

    @Override
    public Secretary findById(int secretaryId) throws SQLException, NotFoundException {
        String sql = "SELECT secretaryId, studentId, sectionId FROM Secretary WHERE secretaryId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, secretaryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("Secretary", secretaryId);
            }
        }
    }

    @Override
    public Secretary findByStudentId(int studentId) throws SQLException, NotFoundException {
        String sql = "SELECT secretaryId, studentId, sectionId FROM Secretary WHERE studentId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("Secretary", "studentId=" + studentId);
            }
        }
    }

    @Override
    public List<Secretary> findBySection(int sectionId) throws SQLException {
        String sql = "SELECT secretaryId, studentId, sectionId FROM Secretary WHERE sectionId = ?";
        List<Secretary> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<Secretary> findAll() throws SQLException {
        String sql = "SELECT secretaryId, studentId, sectionId FROM Secretary";
        List<Secretary> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }
}
