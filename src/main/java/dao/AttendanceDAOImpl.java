package dao;

import core.Student;
import core.User;
import junction.Attendance;
import junction.ClassSession;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import lookup.AttendanceStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAOImpl implements AttendanceDAO {

    private final ClassSessionDAO sessionDAO;
    private final StudentDAO studentDAO;
    private final UserDAO userDAO;

    public AttendanceDAOImpl(ClassSessionDAO sessionDAO, StudentDAO studentDAO, UserDAO userDAO) {
        this.sessionDAO = sessionDAO;
        this.studentDAO = studentDAO;
        this.userDAO = userDAO;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Attendance mapRow(ResultSet rs) throws SQLException {
        try {
            ClassSession session = sessionDAO.findById(rs.getInt("sessionId"));
            Student student = studentDAO.findById(rs.getInt("studentId"));
            AttendanceStatus status = AttendanceStatus.fromId(rs.getInt("statusId"));
            User recordedBy = userDAO.findById(rs.getInt("recordedByUserId"));
            return Attendance.builder()
                    .attendanceId(rs.getInt("attendanceId"))
                    .session(session)
                    .student(student)
                    .status(status)
                    .recordedBy(recordedBy)
                    .build();
        } catch (ValidationException e) {
            throw new SQLException("Invalid statusId in database for attendanceId=" + rs.getInt("attendanceId") + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void insert(Attendance attendance) throws SQLException, DuplicateEntryException {
        String sql = "INSERT INTO Attendance (sessionId, studentId, statusId, recordedByUserId) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, attendance.session().sessionId());
            ps.setInt(2, attendance.student().studentId());
            ps.setInt(3, attendance.status().getStatusId());
            ps.setInt(4, attendance.recordedBy().userId());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("Attendance",
                    "sessionId+studentId",
                    attendance.session().sessionId() + "+" + attendance.student().studentId());
        }
    }

    @Override
    public void update(Attendance attendance) throws SQLException, NotFoundException {
        String sql = "UPDATE Attendance SET sessionId = ?, studentId = ?, statusId = ?, recordedByUserId = ? WHERE attendanceId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, attendance.session().sessionId());
            ps.setInt(2, attendance.student().studentId());
            ps.setInt(3, attendance.status().getStatusId());
            ps.setInt(4, attendance.recordedBy().userId());
            ps.setInt(5, attendance.attendanceId());
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("Attendance", attendance.attendanceId());
            }
        }
    }

    @Override
    public void delete(int attendanceId) throws SQLException, NotFoundException {
        String sql = "DELETE FROM Attendance WHERE attendanceId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, attendanceId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("Attendance", attendanceId);
            }
        }
    }

    @Override
    public Attendance findById(int attendanceId) throws SQLException, NotFoundException {
        String sql = "SELECT attendanceId, sessionId, studentId, statusId, recordedByUserId FROM Attendance WHERE attendanceId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, attendanceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("Attendance", attendanceId);
            }
        }
    }

    @Override
    public List<Attendance> findBySession(int sessionId) throws SQLException {
        return queryList("SELECT attendanceId, sessionId, studentId, statusId, recordedByUserId FROM Attendance WHERE sessionId = ?", sessionId);
    }

    @Override
    public List<Attendance> findByStudent(int studentId) throws SQLException {
        return queryList("SELECT attendanceId, sessionId, studentId, statusId, recordedByUserId FROM Attendance WHERE studentId = ? ORDER BY attendanceId DESC", studentId);
    }

    @Override
    public List<Attendance> findByStudentAndCourse(int studentId, int courseId) throws SQLException {
        String sql = """
                SELECT a.attendanceId, a.sessionId, a.studentId, a.statusId, a.recordedByUserId
                FROM Attendance a
                JOIN ClassSession cs ON a.sessionId = cs.sessionId
                WHERE a.studentId = ? AND cs.courseId = ?
                ORDER BY cs.sessionDate
                """;
        List<Attendance> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<Attendance> findByStudentAndDateRange(int studentId, LocalDate from, LocalDate to) throws SQLException {
        String sql = """
                SELECT a.attendanceId, a.sessionId, a.studentId, a.statusId, a.recordedByUserId
                FROM Attendance a
                JOIN ClassSession cs ON a.sessionId = cs.sessionId
                WHERE a.studentId = ? AND cs.sessionDate BETWEEN ? AND ?
                ORDER BY cs.sessionDate
                """;
        List<Attendance> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<Attendance> findAll() throws SQLException {
        String sql = "SELECT attendanceId, sessionId, studentId, statusId, recordedByUserId FROM Attendance ORDER BY attendanceId DESC";
        List<Attendance> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private List<Attendance> queryList(String sql, int id) throws SQLException {
        List<Attendance> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }
}
