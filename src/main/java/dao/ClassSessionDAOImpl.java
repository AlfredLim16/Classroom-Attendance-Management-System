package dao;

import core.Course;
import core.Professor;
import core.SchoolEvent;
import core.Section;
import junction.ClassSession;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import lookup.ContextType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClassSessionDAOImpl implements ClassSessionDAO {

    private final CourseDAO courseDAO;
    private final SectionDAO sectionDAO;
    private final ProfessorDAO professorDAO;
    private final SchoolEventDAO schoolEventDAO;

    public ClassSessionDAOImpl(CourseDAO courseDAO, SectionDAO sectionDAO,
                                ProfessorDAO professorDAO, SchoolEventDAO schoolEventDAO) {
        this.courseDAO = courseDAO;
        this.sectionDAO = sectionDAO;
        this.professorDAO = professorDAO;
        this.schoolEventDAO = schoolEventDAO;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private ClassSession mapRow(ResultSet rs) throws SQLException {
        try {
            Course course = courseDAO.findById(rs.getInt("courseId"));
            Section section = sectionDAO.findById(rs.getInt("sectionId"));
            Professor professor = professorDAO.findById(rs.getInt("professorId"));
            ContextType contextType = ContextType.fromId(rs.getInt("contextId"));

            // eventId is nullable — only present for SCHOOL_EVENT sessions
            SchoolEvent event = null;
            int eventId = rs.getInt("eventId");
            if (!rs.wasNull()) {
                event = schoolEventDAO.findById(eventId);
            }

            return ClassSession.builder()
                    .sessionId(rs.getInt("sessionId"))
                    .course(course)
                    .section(section)
                    .professor(professor)
                    .sessionDate(rs.getDate("sessionDate").toLocalDate())
                    .startTime(rs.getTime("startTime").toLocalTime())
                    .endTime(rs.getTime("endTime").toLocalTime())
                    .contextType(contextType)
                    .event(event)
                    .build();
        } catch (ValidationException e) {
            throw new SQLException("Invalid contextId in database for sessionId=" + rs.getInt("sessionId") + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void insert(ClassSession session) throws SQLException, DuplicateEntryException {
        String sql = "INSERT INTO ClassSession (courseId, sectionId, professorId, sessionDate, startTime, endTime, contextId, eventId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, session.course().courseId());
            ps.setInt(2, session.section().sectionId());
            ps.setInt(3, session.professor().professorId());
            ps.setDate(4, Date.valueOf(session.sessionDate()));
            ps.setTime(5, Time.valueOf(session.startTime()));
            ps.setTime(6, Time.valueOf(session.endTime()));
            ps.setInt(7, session.contextType().getContextId());
            if (session.event() != null) {
                ps.setInt(8, session.event().eventId());
            } else {
                ps.setNull(8, Types.INTEGER);
            }
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("ClassSession", "sessionId", session.sessionId());
        }
    }

    @Override
    public void update(ClassSession session) throws SQLException, NotFoundException {
        String sql = "UPDATE ClassSession SET courseId = ?, sectionId = ?, professorId = ?, sessionDate = ?, startTime = ?, endTime = ?, contextId = ?, eventId = ? WHERE sessionId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, session.course().courseId());
            ps.setInt(2, session.section().sectionId());
            ps.setInt(3, session.professor().professorId());
            ps.setDate(4, Date.valueOf(session.sessionDate()));
            ps.setTime(5, Time.valueOf(session.startTime()));
            ps.setTime(6, Time.valueOf(session.endTime()));
            ps.setInt(7, session.contextType().getContextId());
            if (session.event() != null) {
                ps.setInt(8, session.event().eventId());
            } else {
                ps.setNull(8, Types.INTEGER);
            }
            ps.setInt(9, session.sessionId());
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("ClassSession", session.sessionId());
            }
        }
    }

    @Override
    public void delete(int sessionId) throws SQLException, NotFoundException {
        String sql = "DELETE FROM ClassSession WHERE sessionId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("ClassSession", sessionId);
            }
        }
    }

    @Override
    public ClassSession findById(int sessionId) throws SQLException, NotFoundException {
        String sql = "SELECT sessionId, courseId, sectionId, professorId, sessionDate, startTime, endTime, contextId, eventId FROM ClassSession WHERE sessionId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("ClassSession", sessionId);
            }
        }
    }

    @Override
    public List<ClassSession> findByCourse(int courseId) throws SQLException {
        return queryList("SELECT sessionId, courseId, sectionId, professorId, sessionDate, startTime, endTime, contextId, eventId FROM ClassSession WHERE courseId = ? ORDER BY sessionDate", courseId);
    }

    @Override
    public List<ClassSession> findBySection(int sectionId) throws SQLException {
        return queryList("SELECT sessionId, courseId, sectionId, professorId, sessionDate, startTime, endTime, contextId, eventId FROM ClassSession WHERE sectionId = ? ORDER BY sessionDate", sectionId);
    }

    @Override
    public List<ClassSession> findByProfessor(int professorId) throws SQLException {
        return queryList("SELECT sessionId, courseId, sectionId, professorId, sessionDate, startTime, endTime, contextId, eventId FROM ClassSession WHERE professorId = ? ORDER BY sessionDate", professorId);
    }

    @Override
    public List<ClassSession> findByDate(LocalDate date) throws SQLException {
        String sql = "SELECT sessionId, courseId, sectionId, professorId, sessionDate, startTime, endTime, contextId, eventId FROM ClassSession WHERE sessionDate = ?";
        List<ClassSession> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<ClassSession> findByDateRange(LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT sessionId, courseId, sectionId, professorId, sessionDate, startTime, endTime, contextId, eventId FROM ClassSession WHERE sessionDate BETWEEN ? AND ? ORDER BY sessionDate";
        List<ClassSession> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<ClassSession> findAll() throws SQLException {
        String sql = "SELECT sessionId, courseId, sectionId, professorId, sessionDate, startTime, endTime, contextId, eventId FROM ClassSession ORDER BY sessionDate";
        List<ClassSession> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private List<ClassSession> queryList(String sql, int id) throws SQLException {
        List<ClassSession> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }
}
