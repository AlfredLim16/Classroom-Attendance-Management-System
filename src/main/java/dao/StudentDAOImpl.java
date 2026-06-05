package dao;

import core.Program;
import core.Section;
import core.Student;
import core.User;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import lookup.YearLevel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAOImpl implements StudentDAO {

    private final UserDAO userDAO;
    private final ProgramDAO programDAO;
    private final SectionDAO sectionDAO;

    public StudentDAOImpl(UserDAO userDAO, ProgramDAO programDAO, SectionDAO sectionDAO) {
        this.userDAO = userDAO;
        this.programDAO = programDAO;
        this.sectionDAO = sectionDAO;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        try {
            User user = userDAO.findById(rs.getInt("userId"));
            Program program = programDAO.findById(rs.getInt("programId"));
            YearLevel yearLevel = YearLevel.fromId(rs.getInt("yearLevelId"));
            Section section = sectionDAO.findById(rs.getInt("sectionId"));
            return Student.builder()
                    .studentId(rs.getInt("studentId"))
                    .user(user)
                    .studentNumber(rs.getString("studentNumber"))
                    .firstName(rs.getString("firstName"))
                    .middleName(rs.getString("middleName"))
                    .lastName(rs.getString("lastName"))
                    .program(program)
                    .yearLevel(yearLevel)
                    .section(section)
                    .build();
        } catch (ValidationException e) {
            throw new SQLException("Invalid yearLevelId in database for studentId=" + rs.getInt("studentId") + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void insert(Student student) throws SQLException, DuplicateEntryException {
        String sql = "INSERT INTO Student (userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, student.user().userId());
            ps.setString(2, student.studentNumber());
            ps.setString(3, student.firstName());
            ps.setString(4, student.middleName());
            ps.setString(5, student.lastName());
            ps.setInt(6, student.program().programId());
            ps.setInt(7, student.yearLevel().getYearLevelId());
            ps.setInt(8, student.section().sectionId());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("Student", "studentNumber", student.studentNumber());
        }
    }

    @Override
    public void update(Student student) throws SQLException, NotFoundException {
        String sql = "UPDATE Student SET userId = ?, studentNumber = ?, firstName = ?, middleName = ?, lastName = ?, programId = ?, yearLevelId = ?, sectionId = ? WHERE studentId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, student.user().userId());
            ps.setString(2, student.studentNumber());
            ps.setString(3, student.firstName());
            ps.setString(4, student.middleName());
            ps.setString(5, student.lastName());
            ps.setInt(6, student.program().programId());
            ps.setInt(7, student.yearLevel().getYearLevelId());
            ps.setInt(8, student.section().sectionId());
            ps.setInt(9, student.studentId());
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("Student", student.studentId());
            }
        }
    }

    @Override
    public void delete(int studentId) throws SQLException, NotFoundException {
        String sql = "DELETE FROM Student WHERE studentId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("Student", studentId);
            }
        }
    }

    @Override
    public Student findById(int studentId) throws SQLException, NotFoundException {
        String sql = "SELECT studentId, userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId FROM Student WHERE studentId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("Student", studentId);
            }
        }
    }

    @Override
    public Student findByStudentNumber(String studentNumber) throws SQLException, NotFoundException {
        String sql = "SELECT studentId, userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId FROM Student WHERE studentNumber = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, studentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("Student", studentNumber);
            }
        }
    }

    @Override
    public Student findByUserId(int userId) throws SQLException, NotFoundException {
        String sql = "SELECT studentId, userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId FROM Student WHERE userId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("Student", "userId=" + userId);
            }
        }
    }

    @Override
    public List<Student> findAll() throws SQLException {
        String sql = "SELECT studentId, userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId FROM Student ORDER BY lastName, firstName";
        List<Student> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Student> findBySection(int sectionId) throws SQLException {
        String sql = "SELECT studentId, userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId FROM Student WHERE sectionId = ? ORDER BY lastName, firstName";
        List<Student> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }
}
