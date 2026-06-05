package dao;

import core.Course;
import junction.QuizLabSchedule;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import lookup.QuizType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizLabScheduleDAOImpl implements QuizLabScheduleDAO {

    private final CourseDAO courseDAO;

    public QuizLabScheduleDAOImpl(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private QuizLabSchedule mapRow(ResultSet rs) throws SQLException {
        try {
            Course course = courseDAO.findById(rs.getInt("courseId"));
            QuizType quizType = QuizType.fromId(rs.getInt("quizTypeId"));
            return QuizLabSchedule.builder()
                    .quizId(rs.getInt("quizId"))
                    .course(course)
                    .quizDate(rs.getDate("quizDate").toLocalDate())
                    .quizType(quizType)
                    .build();
        } catch (ValidationException e) {
            throw new SQLException("Invalid quizTypeId in database for quizId=" + rs.getInt("quizId") + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void insert(QuizLabSchedule schedule) throws SQLException, DuplicateEntryException {
        String sql = "INSERT INTO QuizLabSchedule (courseId, quizDate, quizTypeId) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, schedule.course().courseId());
            ps.setDate(2, Date.valueOf(schedule.quizDate()));
            ps.setInt(3, schedule.quizType().getQuizTypeId());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("QuizLabSchedule", "courseId+quizDate",
                    schedule.course().courseId() + "+" + schedule.quizDate());
        }
    }

    @Override
    public void update(QuizLabSchedule schedule) throws SQLException, NotFoundException {
        String sql = "UPDATE QuizLabSchedule SET courseId = ?, quizDate = ?, quizTypeId = ? WHERE quizId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, schedule.course().courseId());
            ps.setDate(2, Date.valueOf(schedule.quizDate()));
            ps.setInt(3, schedule.quizType().getQuizTypeId());
            ps.setInt(4, schedule.quizId());
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("QuizLabSchedule", schedule.quizId());
            }
        }
    }

    @Override
    public void delete(int quizId) throws SQLException, NotFoundException {
        String sql = "DELETE FROM QuizLabSchedule WHERE quizId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, quizId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("QuizLabSchedule", quizId);
            }
        }
    }

    @Override
    public QuizLabSchedule findById(int quizId) throws SQLException, NotFoundException {
        String sql = "SELECT quizId, courseId, quizDate, quizTypeId FROM QuizLabSchedule WHERE quizId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("QuizLabSchedule", quizId);
            }
        }
    }

    @Override
    public List<QuizLabSchedule> findByCourse(int courseId) throws SQLException {
        String sql = "SELECT quizId, courseId, quizDate, quizTypeId FROM QuizLabSchedule WHERE courseId = ? ORDER BY quizDate";
        List<QuizLabSchedule> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<QuizLabSchedule> findAll() throws SQLException {
        String sql = "SELECT quizId, courseId, quizDate, quizTypeId FROM QuizLabSchedule ORDER BY quizDate";
        List<QuizLabSchedule> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }
}
