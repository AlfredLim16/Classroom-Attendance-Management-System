package dao;

import junction.Attendance;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceDAO {
    void insert(Attendance attendance) throws SQLException, DuplicateEntryException;
    void update(Attendance attendance) throws SQLException, NotFoundException;
    void delete(int attendanceId) throws SQLException, NotFoundException;
    Attendance findById(int attendanceId) throws SQLException, NotFoundException;
    List<Attendance> findBySession(int sessionId) throws SQLException;
    List<Attendance> findByStudent(int studentId) throws SQLException;
    /** Attendance for a student in a specific course — used for per-subject view. */
    List<Attendance> findByStudentAndCourse(int studentId, int courseId) throws SQLException;
    /** Date-range filter — backs week/month/semester filtering in the UI. */
    List<Attendance> findByStudentAndDateRange(int studentId, LocalDate from, LocalDate to) throws SQLException;
    List<Attendance> findAll() throws SQLException;
}
