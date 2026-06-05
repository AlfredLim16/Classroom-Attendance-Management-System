package dao;

import core.Course;
import core.Program;
import core.Semester;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import lookup.YearLevel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAOImpl implements CourseDAO {

    private final ProgramDAO programDAO;
    private final SemesterDAO semesterDAO;

    public CourseDAOImpl(ProgramDAO programDAO, SemesterDAO semesterDAO) {
        this.programDAO = programDAO;
        this.semesterDAO = semesterDAO;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Course mapRow(ResultSet rs) throws SQLException {
        try {
            Program program = programDAO.findById(rs.getInt("programId"));
            Semester semester = semesterDAO.findById(rs.getInt("semesterId"));
            YearLevel yearLevel = YearLevel.fromId(rs.getInt("yearLevelId"));
            return Course.builder()
                    .courseId(rs.getInt("courseId"))
                    .program(program)
                    .courseCode(rs.getString("courseCode"))
                    .courseName(rs.getString("courseName"))
                    .units((byte) rs.getInt("units"))
                    .semester(semester)
                    .yearLevel(yearLevel)
                    .build();
        } catch (ValidationException e) {
            throw new SQLException("Invalid yearLevelId in database for courseId=" + rs.getInt("courseId") + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void insert(Course course) throws SQLException, DuplicateEntryException {
        String sql = "INSERT INTO Course (programId, courseCode, courseName, units, semesterId, yearLevelId) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, course.program().programId());
            ps.setString(2, course.courseCode());
            ps.setString(3, course.courseName());
            ps.setInt(4, course.units());
            ps.setInt(5, course.semester().semesterId());
            ps.setInt(6, course.yearLevel().getYearLevelId());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("Course", "courseCode", course.courseCode());
        }
    }

    @Override
    public void update(Course course) throws SQLException, NotFoundException {
        String sql = "UPDATE Course SET programId = ?, courseCode = ?, courseName = ?, units = ?, semesterId = ?, yearLevelId = ? WHERE courseId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, course.program().programId());
            ps.setString(2, course.courseCode());
            ps.setString(3, course.courseName());
            ps.setInt(4, course.units());
            ps.setInt(5, course.semester().semesterId());
            ps.setInt(6, course.yearLevel().getYearLevelId());
            ps.setInt(7, course.courseId());
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("Course", course.courseId());
            }
        }
    }

    @Override
    public void delete(int courseId) throws SQLException, NotFoundException {
        String sql = "DELETE FROM Course WHERE courseId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("Course", courseId);
            }
        }
    }

    @Override
    public Course findById(int courseId) throws SQLException, NotFoundException {
        String sql = "SELECT courseId, programId, courseCode, courseName, units, semesterId, yearLevelId FROM Course WHERE courseId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("Course", courseId);
            }
        }
    }

    @Override
    public List<Course> findAll() throws SQLException {
        String sql = "SELECT courseId, programId, courseCode, courseName, units, semesterId, yearLevelId FROM Course ORDER BY courseCode";
        List<Course> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Course> findByProgram(int programId) throws SQLException {
        String sql = "SELECT courseId, programId, courseCode, courseName, units, semesterId, yearLevelId FROM Course WHERE programId = ? ORDER BY courseCode";
        List<Course> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, programId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<Course> findBySemester(int semesterId) throws SQLException {
        String sql = "SELECT courseId, programId, courseCode, courseName, units, semesterId, yearLevelId FROM Course WHERE semesterId = ? ORDER BY courseCode";
        List<Course> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, semesterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }
}
