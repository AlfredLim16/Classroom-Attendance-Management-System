package services;

import core.Student;
import core.User;
import dao.*;
import exceptions.AttendancePolicyException;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import junction.Attendance;
import junction.AttendancePolicy;
import junction.ClassSession;
import lookup.AttendanceStatus;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Core business logic for recording and querying attendance.
 * Enforces the attendance policy (late→absent conversion, drop rule).
 */
public class AttendanceService {

    private final AttendanceDAO attendanceDAO;
    private final AttendancePolicyDAO policyDAO;
    private final ClassSessionDAO sessionDAO;
    private final StudentDAO studentDAO;
    private final UserDAO userDAO;

    public AttendanceService() {
        DatabaseConnection db = null;
        try { db = DatabaseConnection.getInstance(); } catch (SQLException ignored) {}
        ProgramDAO programDAO             = new ProgramDAOImpl();
        SemesterDAO semesterDAO           = new SemesterDAOImpl();
        SchoolEventDAO schoolEventDAO     = new SchoolEventDAOImpl();
        SectionDAO sectionDAO             = new SectionDAOImpl(programDAO);
        CourseDAO courseDAO               = new CourseDAOImpl(programDAO, semesterDAO);
        this.userDAO                      = new UserDAOImpl();
        this.studentDAO                   = new StudentDAOImpl(userDAO, programDAO, sectionDAO);
        ProfessorDAO professorDAO         = new ProfessorDAOImpl(userDAO);
        this.sessionDAO                   = new ClassSessionDAOImpl(courseDAO, sectionDAO, professorDAO, schoolEventDAO);
        this.attendanceDAO                = new AttendanceDAOImpl(sessionDAO, studentDAO, userDAO);
        this.policyDAO                    = new AttendancePolicyDAOImpl(courseDAO);
    }

    public AttendanceService(AttendanceDAO attendanceDAO, AttendancePolicyDAO policyDAO,
                              ClassSessionDAO sessionDAO, StudentDAO studentDAO, UserDAO userDAO) {
        this.attendanceDAO = attendanceDAO;
        this.policyDAO     = policyDAO;
        this.sessionDAO    = sessionDAO;
        this.studentDAO    = studentDAO;
        this.userDAO       = userDAO;
    }

    /**
     * Records attendance for a student in a session.
     * After recording, evaluates the attendance policy and throws
     * AttendancePolicyException if the student must be dropped.
     */
    public void recordAttendance(ClassSession session, Student student,
                                  AttendanceStatus status, User recordedBy)
            throws SQLException, DuplicateEntryException, AttendancePolicyException {

        Attendance attendance = Attendance.builder()
                .session(session)
                .student(student)
                .status(status)
                .recordedBy(recordedBy)
                .build();

        attendanceDAO.insert(attendance);
        evaluatePolicy(student, session);
    }

    /**
     * Updates an existing attendance record (e.g. correcting a mistake).
     */
    public void updateAttendance(Attendance attendance)
            throws SQLException, NotFoundException, AttendancePolicyException {
        attendanceDAO.update(attendance);
        evaluatePolicy(attendance.student(), attendance.session());
    }

    /**
     * Returns all attendance records for a student in a specific course.
     */
    public List<Attendance> getStudentAttendanceByCourse(int studentId, int courseId)
            throws SQLException {
        return attendanceDAO.findByStudentAndCourse(studentId, courseId);
    }

    /**
     * Returns attendance records filtered by date range (backs week/month/semester view).
     */
    public List<Attendance> getStudentAttendanceByDateRange(int studentId, LocalDate from, LocalDate to)
            throws SQLException {
        return attendanceDAO.findByStudentAndDateRange(studentId, from, to);
    }

    /**
     * Returns all attendance records for a given session.
     */
    public List<Attendance> getSessionAttendance(int sessionId) throws SQLException {
        return attendanceDAO.findBySession(sessionId);
    }

    /**
     * Returns all sessions taught by a professor.
     */
    public List<ClassSession> getProfessorSessions(int professorId) throws SQLException {
        return sessionDAO.findByProfessor(professorId);
    }

    /**
     * Returns sessions in a date range — used by week/month filters.
     */
    public List<ClassSession> getSessionsByDateRange(LocalDate from, LocalDate to) throws SQLException {
        return sessionDAO.findByDateRange(from, to);
    }

    /**
     * Counts absences and lates for a student in a course and enforces
     * the configured policy. Throws AttendancePolicyException if the student
     * has exceeded the allowed absences and should be dropped.
     */
    private void evaluatePolicy(Student student, ClassSession session)
            throws SQLException, AttendancePolicyException {

        int courseId = session.course().courseId();

        AttendancePolicy policy;
        try {
            policy = policyDAO.findActiveByCourse(courseId);
        } catch (NotFoundException e) {
            return; // no active policy — nothing to enforce
        }

        List<Attendance> records = attendanceDAO.findByStudentAndCourse(student.studentId(), courseId);

        long lateCount   = records.stream().filter(a -> a.status() == AttendanceStatus.LATE).count();
        long absentCount = records.stream().filter(a -> a.status() == AttendanceStatus.ABSENT).count();

        // Convert accumulated lates into equivalent absents
        long lateEquivalent = lateCount / policy.latesEqualToAbsent();
        long totalAbsents   = absentCount + lateEquivalent;

        if (totalAbsents >= policy.absentsEqualToDropped()) {
            throw new AttendancePolicyException(
                    "Student " + student.getFullName() + " has been dropped from "
                    + session.course().courseCode() + " due to excessive absences ("
                    + totalAbsents + "/" + policy.absentsEqualToDropped() + ")",
                    student.studentId(),
                    courseId
            );
        }
    }

    /**
     * Calculates attendance summary counts for a student in a course.
     * Returns int[]{present, late, absent, excused}.
     */
    public int[] getAttendanceSummary(int studentId, int courseId) throws SQLException {
        List<Attendance> records = attendanceDAO.findByStudentAndCourse(studentId, courseId);
        int present = 0, late = 0, absent = 0, excused = 0;
        for (Attendance a : records) {
            switch (a.status()) {
                case PRESENT -> present++;
                case LATE    -> late++;
                case ABSENT  -> absent++;
                case EXCUSED -> excused++;
            }
        }
        return new int[]{present, late, absent, excused};
    }
}
