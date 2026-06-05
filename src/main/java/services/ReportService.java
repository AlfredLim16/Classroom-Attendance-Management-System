package services;

import dao.*;
import junction.Attendance;
import junction.ClassSession;
import junction.StudentCourse;
import core.Student;
import lookup.AttendanceStatus;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * Aggregates data for reports shown in the UI panels.
 * All methods return plain data structures (List / Map) so UI layers
 * can render them without coupling to DAO types.
 */
public class ReportService {

    private final AttendanceDAO attendanceDAO;
    private final ClassSessionDAO sessionDAO;
    private final StudentCourseDAO studentCourseDAO;
    private final StudentDAO studentDAO;
    private final AttendancePolicyDAO policyDAO;

    public ReportService() {
        ProgramDAO programDAO         = new ProgramDAOImpl();
        SemesterDAO semesterDAO       = new SemesterDAOImpl();
        SchoolEventDAO schoolEventDAO = new SchoolEventDAOImpl();
        SectionDAO sectionDAO         = new SectionDAOImpl(programDAO);
        CourseDAO courseDAO           = new CourseDAOImpl(programDAO, semesterDAO);
        UserDAO userDAO               = new UserDAOImpl();
        this.studentDAO               = new StudentDAOImpl(userDAO, programDAO, sectionDAO);
        ProfessorDAO professorDAO     = new ProfessorDAOImpl(userDAO);
        this.sessionDAO               = new ClassSessionDAOImpl(courseDAO, sectionDAO, professorDAO, schoolEventDAO);
        this.attendanceDAO            = new AttendanceDAOImpl(sessionDAO, studentDAO, userDAO);
        this.studentCourseDAO         = new StudentCourseDAOImpl(studentDAO, courseDAO, semesterDAO);
        this.policyDAO                = new AttendancePolicyDAOImpl(courseDAO);
    }

    public ReportService(AttendanceDAO attendanceDAO, ClassSessionDAO sessionDAO,
                          StudentCourseDAO studentCourseDAO, StudentDAO studentDAO,
                          AttendancePolicyDAO policyDAO) {
        this.attendanceDAO    = attendanceDAO;
        this.sessionDAO       = sessionDAO;
        this.studentCourseDAO = studentCourseDAO;
        this.studentDAO       = studentDAO;
        this.policyDAO        = policyDAO;
    }

    /**
     * Returns attendance summary for every student enrolled in a course.
     * Map key = Student, value = int[]{present, late, absent, excused}.
     */
    public Map<Student, int[]> getCourseAttendanceSummary(int courseId) throws SQLException {
        List<StudentCourse> enrollments = studentCourseDAO.findByCourse(courseId);
        Map<Student, int[]> summary = new LinkedHashMap<>();
        for (StudentCourse sc : enrollments) {
            Student student = sc.student();
            List<Attendance> records = attendanceDAO.findByStudentAndCourse(student.studentId(), courseId);
            int present = 0, late = 0, absent = 0, excused = 0;
            for (Attendance a : records) {
                switch (a.status()) {
                    case PRESENT -> present++;
                    case LATE    -> late++;
                    case ABSENT  -> absent++;
                    case EXCUSED -> excused++;
                }
            }
            summary.put(student, new int[]{present, late, absent, excused});
        }
        return summary;
    }

    /**
     * Returns attendance records for a date range — backs week/month/semester filter.
     */
    public List<Attendance> getAttendanceByDateRange(int studentId, LocalDate from, LocalDate to)
            throws SQLException {
        return attendanceDAO.findByStudentAndDateRange(studentId, from, to);
    }

    /**
     * Returns all sessions in a date range for institution-wide reporting.
     */
    public List<ClassSession> getSessionsByDateRange(LocalDate from, LocalDate to) throws SQLException {
        return sessionDAO.findByDateRange(from, to);
    }

    /**
     * Returns all attendance records for a single session (professor view).
     */
    public List<Attendance> getSessionAttendance(int sessionId) throws SQLException {
        return attendanceDAO.findBySession(sessionId);
    }

    /**
     * Builds a simple text-based attendance report for a course.
     * Returns list of lines ready to display in a JTextArea or export.
     */
    public List<String> buildCourseReport(int courseId, String courseCode) throws SQLException {
        Map<Student, int[]> summary = getCourseAttendanceSummary(courseId);
        List<String> lines = new ArrayList<>();
        lines.add("Attendance Report — " + courseCode);
        lines.add("Generated: " + LocalDate.now());
        lines.add("─".repeat(60));
        lines.add(String.format("%-30s %7s %7s %7s %7s", "Student", "Present", "Late", "Absent", "Excused"));
        lines.add("─".repeat(60));
        for (Map.Entry<Student, int[]> entry : summary.entrySet()) {
            int[] counts = entry.getValue();
            lines.add(String.format("%-30s %7d %7d %7d %7d",
                    entry.getKey().getFullName(), counts[0], counts[1], counts[2], counts[3]));
        }
        lines.add("─".repeat(60));
        return lines;
    }
}
