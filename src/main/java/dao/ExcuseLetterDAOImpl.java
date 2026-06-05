package dao;

import core.Course;
import core.Student;
import core.User;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import junction.ExcuseLetter;
import lookup.ExcuseStatus;

public class ExcuseLetterDAOImpl implements ExcuseLetterDAO {

    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private final UserDAO userDAO;

    public ExcuseLetterDAOImpl(StudentDAO studentDAO, CourseDAO courseDAO, UserDAO userDAO) {
        this.studentDAO = studentDAO;
        this.courseDAO = courseDAO;
        this.userDAO = userDAO;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private ExcuseLetter mapRow(ResultSet rs) throws SQLException {
        try {
            Student student = studentDAO.findById(rs.getInt("studentId"));
            Course course = courseDAO.findById(rs.getInt("courseId"));
            ExcuseStatus status = ExcuseStatus.fromId(rs.getInt("excuseStatusId"));

            User reviewedBy = null;
            int reviewedByUserId = rs.getInt("reviewedByUserId");
            if (!rs.wasNull()) {
                reviewedBy = userDAO.findById(reviewedByUserId);
            }
            Timestamp reviewedTs = rs.getTimestamp("reviewedDate");

            return ExcuseLetter.builder()
                    .excuseId(rs.getInt("excuseId"))
                    .student(student)
                    .course(course)
                    .absentDate(rs.getDate("absentDate").toLocalDate())
                    .reason(rs.getString("reason"))
                    .supportingDocumentPath(rs.getString("supportingDocumentPath"))
                    .status(status)
                    .reviewedBy(reviewedBy)
                    .submittedDate(rs.getTimestamp("submittedDate").toLocalDateTime())
                    .reviewedDate(reviewedTs != null ? reviewedTs.toLocalDateTime() : null)
                    .build();
        } catch (ValidationException e) {
            throw new SQLException("Invalid excuseStatusId in database for excuseId=" + rs.getInt("excuseId") + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void insert(ExcuseLetter letter) throws SQLException, DuplicateEntryException {
        String sql = """
                INSERT INTO ExcuseLetter
                (studentId, courseId, absentDate, reason, supportingDocumentPath, excuseStatusId, reviewedByUserId, submittedDate, reviewedDate)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, letter.student().studentId());
            ps.setInt(2, letter.course().courseId());
            ps.setDate(3, Date.valueOf(letter.absentDate()));
            ps.setString(4, letter.reason());
            ps.setString(5, letter.supportingDocumentPath());
            ps.setInt(6, letter.status().getExcuseStatusId());
            if (letter.reviewedBy() != null) {
                ps.setInt(7, letter.reviewedBy().userId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setTimestamp(8, Timestamp.valueOf(letter.submittedDate()));
            if (letter.reviewedDate() != null) {
                ps.setTimestamp(9, Timestamp.valueOf(letter.reviewedDate()));
            } else {
                ps.setNull(9, Types.TIMESTAMP);
            }
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("ExcuseLetter", "studentId+courseId+absentDate",
                    letter.student().studentId() + "+" + letter.course().courseId() + "+" + letter.absentDate());
        }
    }

    @Override
    public void update(ExcuseLetter letter) throws SQLException, NotFoundException {
        String sql = """
                UPDATE ExcuseLetter
                SET studentId = ?, courseId = ?, absentDate = ?, reason = ?, supportingDocumentPath = ?,
                    excuseStatusId = ?, reviewedByUserId = ?, submittedDate = ?, reviewedDate = ?
                WHERE excuseId = ?
                """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, letter.student().studentId());
            ps.setInt(2, letter.course().courseId());
            ps.setDate(3, Date.valueOf(letter.absentDate()));
            ps.setString(4, letter.reason());
            ps.setString(5, letter.supportingDocumentPath());
            ps.setInt(6, letter.status().getExcuseStatusId());
            if (letter.reviewedBy() != null) {
                ps.setInt(7, letter.reviewedBy().userId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setTimestamp(8, Timestamp.valueOf(letter.submittedDate()));
            if (letter.reviewedDate() != null) {
                ps.setTimestamp(9, Timestamp.valueOf(letter.reviewedDate()));
            } else {
                ps.setNull(9, Types.TIMESTAMP);
            }
            ps.setInt(10, letter.excuseId());
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("ExcuseLetter", letter.excuseId());
            }
        }
    }

    @Override
    public void delete(int excuseId) throws SQLException, NotFoundException {
        String sql = "DELETE FROM ExcuseLetter WHERE excuseId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, excuseId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("ExcuseLetter", excuseId);
            }
        }
    }

    @Override
    public ExcuseLetter findById(int excuseId) throws SQLException, NotFoundException {
        String sql = "SELECT excuseId, studentId, courseId, absentDate, reason, supportingDocumentPath, excuseStatusId, reviewedByUserId, submittedDate, reviewedDate FROM ExcuseLetter WHERE excuseId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, excuseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("ExcuseLetter", excuseId);
            }
        }
    }

    @Override
    public List<ExcuseLetter> findByStudent(int studentId) throws SQLException {
        String sql = "SELECT excuseId, studentId, courseId, absentDate, reason, supportingDocumentPath, excuseStatusId, reviewedByUserId, submittedDate, reviewedDate FROM ExcuseLetter WHERE studentId = ? ORDER BY submittedDate DESC";
        return queryList(sql, studentId);
    }

    @Override
    public List<ExcuseLetter> findByStudentAndCourse(int studentId, int courseId) throws SQLException {
        String sql = "SELECT excuseId, studentId, courseId, absentDate, reason, supportingDocumentPath, excuseStatusId, reviewedByUserId, submittedDate, reviewedDate FROM ExcuseLetter WHERE studentId = ? AND courseId = ? ORDER BY absentDate DESC";
        List<ExcuseLetter> list = new ArrayList<>();
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
    public List<ExcuseLetter> findByStatus(ExcuseStatus status) throws SQLException {
        String sql = "SELECT excuseId, studentId, courseId, absentDate, reason, supportingDocumentPath, excuseStatusId, reviewedByUserId, submittedDate, reviewedDate FROM ExcuseLetter WHERE excuseStatusId = ? ORDER BY submittedDate DESC";
        return queryList(sql, status.getExcuseStatusId());
    }

    @Override
    public List<ExcuseLetter> findAll() throws SQLException {
        String sql = "SELECT excuseId, studentId, courseId, absentDate, reason, supportingDocumentPath, excuseStatusId, reviewedByUserId, submittedDate, reviewedDate FROM ExcuseLetter ORDER BY submittedDate DESC";
        List<ExcuseLetter> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private List<ExcuseLetter> queryList(String sql, int id) throws SQLException {
        List<ExcuseLetter> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }
}
