package dao;

import core.Course;
import core.Semester;
import core.Student;
import junction.StudentCourse;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentCourseDAOImpl implements StudentCourseDAO {

    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private final SemesterDAO semesterDAO;

    public StudentCourseDAOImpl(StudentDAO studentDAO, CourseDAO courseDAO, SemesterDAO semesterDAO) {
        this.studentDAO = studentDAO;
        this.courseDAO = courseDAO;
        this.semesterDAO = semesterDAO;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private StudentCourse mapRow(ResultSet rs) throws SQLException {
        Student student = studentDAO.findById(rs.getInt("studentId"));
        Course course = courseDAO.findById(rs.getInt("courseId"));
        Semester semester = semesterDAO.findById(rs.getInt("semesterId"));
        return StudentCourse.builder()
                .studentCourseId(rs.getInt("studentCourseId"))
                .student(student)
                .course(course)
                .semester(semester)
                .build();
    }

    @Override
    public void insert(StudentCourse sc) throws SQLException, DuplicateEntryException {
        String sql = "INSERT INTO StudentCourse (studentId, courseId, semesterId) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, sc.student().studentId());
            ps.setInt(2, sc.course().courseId());
            ps.setInt(3, sc.semester().semesterId());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("StudentCourse",
                    "studentId+courseId+semesterId",
                    sc.student().studentId() + "+" + sc.course().courseId() + "+" + sc.semester().semesterId());
        }
    }

    @Override
    public void delete(int studentCourseId) throws SQLException, NotFoundException {
        String sql = "DELETE FROM StudentCourse WHERE studentCourseId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, studentCourseId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("StudentCourse", studentCourseId);
            }
        }
    }

    @Override
    public StudentCourse findById(int studentCourseId) throws SQLException, NotFoundException {
        String sql = "SELECT studentCourseId, studentId, courseId, semesterId FROM StudentCourse WHERE studentCourseId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, studentCourseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("StudentCourse", studentCourseId);
            }
        }
    }

    @Override
    public List<StudentCourse> findByStudent(int studentId) throws SQLException {
        String sql = "SELECT studentCourseId, studentId, courseId, semesterId FROM StudentCourse WHERE studentId = ?";
        return queryList(sql, studentId);
    }

    @Override
    public List<StudentCourse> findByCourse(int courseId) throws SQLException {
        String sql = "SELECT studentCourseId, studentId, courseId, semesterId FROM StudentCourse WHERE courseId = ?";
        return queryList(sql, courseId);
    }

    @Override
    public List<StudentCourse> findBySemester(int semesterId) throws SQLException {
        String sql = "SELECT studentCourseId, studentId, courseId, semesterId FROM StudentCourse WHERE semesterId = ?";
        return queryList(sql, semesterId);
    }

    @Override
    public List<StudentCourse> findAll() throws SQLException {
        String sql = "SELECT studentCourseId, studentId, courseId, semesterId FROM StudentCourse";
        List<StudentCourse> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private List<StudentCourse> queryList(String sql, int id) throws SQLException {
        List<StudentCourse> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }
}
