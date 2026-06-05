package dao;

import core.Course;
import core.Professor;
import core.Semester;
import junction.ProfessorCourse;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfessorCourseDAOImpl implements ProfessorCourseDAO {

    private final ProfessorDAO professorDAO;
    private final CourseDAO courseDAO;
    private final SemesterDAO semesterDAO;

    public ProfessorCourseDAOImpl(ProfessorDAO professorDAO, CourseDAO courseDAO, SemesterDAO semesterDAO) {
        this.professorDAO = professorDAO;
        this.courseDAO = courseDAO;
        this.semesterDAO = semesterDAO;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private ProfessorCourse mapRow(ResultSet rs) throws SQLException {
        Professor professor = professorDAO.findById(rs.getInt("professorId"));
        Course course = courseDAO.findById(rs.getInt("courseId"));
        Semester semester = semesterDAO.findById(rs.getInt("semesterId"));
        return ProfessorCourse.builder()
                .professorCourseId(rs.getInt("professorCourseId"))
                .professor(professor)
                .course(course)
                .semester(semester)
                .build();
    }

    @Override
    public void insert(ProfessorCourse pc) throws SQLException, DuplicateEntryException {
        String sql = "INSERT INTO ProfessorCourse (professorId, courseId, semesterId) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, pc.professor().professorId());
            ps.setInt(2, pc.course().courseId());
            ps.setInt(3, pc.semester().semesterId());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("ProfessorCourse",
                    "professorId+courseId+semesterId",
                    pc.professor().professorId() + "+" + pc.course().courseId() + "+" + pc.semester().semesterId());
        }
    }

    @Override
    public void delete(int professorCourseId) throws SQLException, NotFoundException {
        String sql = "DELETE FROM ProfessorCourse WHERE professorCourseId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, professorCourseId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("ProfessorCourse", professorCourseId);
            }
        }
    }

    @Override
    public ProfessorCourse findById(int professorCourseId) throws SQLException, NotFoundException {
        String sql = "SELECT professorCourseId, professorId, courseId, semesterId FROM ProfessorCourse WHERE professorCourseId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, professorCourseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("ProfessorCourse", professorCourseId);
            }
        }
    }

    @Override
    public List<ProfessorCourse> findByProfessor(int professorId) throws SQLException {
        String sql = "SELECT professorCourseId, professorId, courseId, semesterId FROM ProfessorCourse WHERE professorId = ?";
        return queryList(sql, professorId);
    }

    @Override
    public List<ProfessorCourse> findByCourse(int courseId) throws SQLException {
        String sql = "SELECT professorCourseId, professorId, courseId, semesterId FROM ProfessorCourse WHERE courseId = ?";
        return queryList(sql, courseId);
    }

    @Override
    public List<ProfessorCourse> findBySemester(int semesterId) throws SQLException {
        String sql = "SELECT professorCourseId, professorId, courseId, semesterId FROM ProfessorCourse WHERE semesterId = ?";
        return queryList(sql, semesterId);
    }

    @Override
    public List<ProfessorCourse> findAll() throws SQLException {
        String sql = "SELECT professorCourseId, professorId, courseId, semesterId FROM ProfessorCourse";
        List<ProfessorCourse> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private List<ProfessorCourse> queryList(String sql, int id) throws SQLException {
        List<ProfessorCourse> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }
}
