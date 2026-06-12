package services;

import core.Student;
import core.User;
import dao.*;
import exceptions.AttendancePolicyException;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import junction.Attendance;
import junction.AttendancePolicy;
import junction.ClassSession;
import lookup.AttendanceStatus;

public class AttendanceService {

    private final AttendanceDAO attendanceDAO;
    private final AttendancePolicyDAO policyDAO;
    private final ClassSessionDAO sessionDAO;
    private final StudentDAO studentDAO;
    private final UserDAO userDAO;

    public AttendanceService(){
        ProgramDAO programDAO = new ProgramDAOImpl();
        SemesterDAO semesterDAO = new SemesterDAOImpl();
        SchoolEventDAO schoolEventDAO = new SchoolEventDAOImpl();
        SectionDAO sectionDAO = new SectionDAOImpl(programDAO);
        CourseDAO courseDAO = new CourseDAOImpl(programDAO, semesterDAO);
        this.userDAO = new UserDAOImpl();
        this.studentDAO = new StudentDAOImpl(userDAO, programDAO, sectionDAO);
        ProfessorDAO professorDAO = new ProfessorDAOImpl(userDAO);
        this.sessionDAO = new ClassSessionDAOImpl(courseDAO, sectionDAO, professorDAO, schoolEventDAO);
        this.attendanceDAO = new AttendanceDAOImpl(sessionDAO, studentDAO, userDAO);
        this.policyDAO = new AttendancePolicyDAOImpl(courseDAO);
    }

    public AttendanceService(AttendanceDAO attendanceDAO, AttendancePolicyDAO policyDAO, ClassSessionDAO sessionDAO, StudentDAO studentDAO, UserDAO userDAO){
        this.attendanceDAO = attendanceDAO;
        this.policyDAO = policyDAO;
        this.sessionDAO = sessionDAO;
        this.studentDAO = studentDAO;
        this.userDAO = userDAO;
    }

    public void recordAttendance(ClassSession session, Student student, AttendanceStatus status, User recordedBy) throws SQLException, DuplicateEntryException, AttendancePolicyException{

        Attendance attendance = Attendance.builder()
            .session(session)
            .student(student)
            .status(status)
            .recordedBy(recordedBy)
            .build();

        attendanceDAO.insert(attendance);
        evaluatePolicy(student, session);
    }

    public void updateAttendance(Attendance attendance) throws SQLException, NotFoundException, AttendancePolicyException{
        attendanceDAO.update(attendance);
        evaluatePolicy(attendance.student(), attendance.session());
    }

    public List<Attendance> getStudentAttendanceByCourse(int studentId, int courseId) throws SQLException{
        return attendanceDAO.findByStudentAndCourse(studentId, courseId);
    }

    public List<Attendance> getStudentAttendanceByDateRange(int studentId, LocalDate from, LocalDate to) throws SQLException{
        return attendanceDAO.findByStudentAndDateRange(studentId, from, to);
    }

    public List<Attendance> getSessionAttendance(int sessionId) throws SQLException{
        return attendanceDAO.findBySession(sessionId);
    }

    public List<ClassSession> getProfessorSessions(int professorId) throws SQLException{
        return sessionDAO.findByProfessor(professorId);
    }

    public List<ClassSession> getSessionsByDateRange(LocalDate from, LocalDate to) throws SQLException{
        return sessionDAO.findByDateRange(from, to);
    }

    private void evaluatePolicy(Student student, ClassSession session) throws SQLException, AttendancePolicyException{

        int courseId = session.course().courseId();

        AttendancePolicy policy;
        try{
            policy = policyDAO.findActiveByCourse(courseId);
        }catch(NotFoundException e){
            return;
        }

        List<Attendance> records = attendanceDAO.findByStudentAndCourse(student.studentId(), courseId);

        long lateCount = records.stream().filter(a -> a.status() == AttendanceStatus.LATE).count();
        long absentCount = records.stream().filter(a -> a.status() == AttendanceStatus.ABSENT).count();

        long lateEquivalent = lateCount / policy.latesEqualToAbsent();
        long totalAbsents = absentCount + lateEquivalent;

        if(totalAbsents >= policy.absentsEqualToDropped()){
            throw new AttendancePolicyException("Student " + student.getFullName() + " has been dropped from " + session.course().courseCode() + " due to excessive absences (" + totalAbsents + "/" + policy.absentsEqualToDropped() + ")", student.studentId(), courseId);
        }
    }

    public int[] getAttendanceSummary(int studentId, int courseId) throws SQLException{
        List<Attendance> records = attendanceDAO.findByStudentAndCourse(studentId, courseId);
        int present = 0, late = 0, absent = 0, excused = 0;
        for(Attendance a : records){
            switch(a.status()){
                case PRESENT ->
                    present++;
                case LATE ->
                    late++;
                case ABSENT ->
                    absent++;
                case EXCUSED ->
                    excused++;
            }
        }
        return new int[]{present, late, absent, excused};
    }
}
