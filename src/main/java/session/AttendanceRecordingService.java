package session;

import excuse.ExcuseLetter;
import excuse.ExcuseLetterDAO;
import excuse.ExcuseStatus;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import user.Student;
import user.StudentCourseDAO;
import user.User;

public class AttendanceRecordingService {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final AttendancePolicyDAO attendancePolicyDAO = new AttendancePolicyDAO();
    private final ClassSessionDAO classSessionDAO = new ClassSessionDAO();
    private final StudentCourseDAO studentCourseDAO = new StudentCourseDAO();
    private final ExcuseLetterDAO excuseLetterDAO = new ExcuseLetterDAO();

    public Attendance recordStudentAttendance(int sessionId, int studentId, AttendanceStatus rawStatus, User recordedBy){
        Optional<ClassSession> sessionOpt = classSessionDAO.findById(sessionId);
        if(sessionOpt.isEmpty()){
            throw new IllegalArgumentException("Class session not found");
        }
        ClassSession session = sessionOpt.get();

        Optional<Attendance> existing = attendanceDAO.findBySessionAndStudent(sessionId, studentId);
        if(existing.isPresent()){
            throw new IllegalStateException("Attendance already recorded for this student in this session");
        }

        AttendanceStatus finalStatus = determineFinalStatus(session, studentId, rawStatus);

        Attendance attendance = Attendance.builder()
            .attendanceId(0)
            .session(session)
            .student(new Student(studentId, null, null, null, null, null, null, null, null))
            .status(finalStatus)
            .recordedBy(recordedBy)
            .build();

        return attendanceDAO.save(attendance);
    }

    private AttendanceStatus determineFinalStatus(ClassSession session, int studentId, AttendanceStatus rawStatus){
        if(rawStatus == AttendanceStatus.ABSENT){
            List<ExcuseLetter> excuses = excuseLetterDAO.findByStudentId(studentId);
            for(ExcuseLetter excuse : excuses){
                if(excuse.status() == ExcuseStatus.APPROVED
                    && excuse.absentDate().equals(session.sessionDate())
                    && excuse.course().courseId() == session.course().courseId()){
                    return AttendanceStatus.EXCUSED;
                }
            }
        }

        if(rawStatus == AttendanceStatus.PRESENT){
            Optional<AttendancePolicy> policyOpt = attendancePolicyDAO.findByCourseId(session.course().courseId());
            if(policyOpt.isPresent()){
                AttendancePolicy policy = policyOpt.get();
                if(policy.active()){
                    LocalTime threshold = session.startTime().plusMinutes(policy.lateThresholdMinutes());
                    if(LocalTime.now().isAfter(threshold)){
                        return AttendanceStatus.LATE;
                    }
                }
            }
        }

        return rawStatus;
    }

    public List<Attendance> getSessionAttendance(int sessionId){
        return attendanceDAO.findBySessionId(sessionId);
    }

    public void bulkRecordAttendance(int sessionId, List<Integer> studentIds, AttendanceStatus status, User recordedBy){
        Optional<ClassSession> sessionOpt = classSessionDAO.findById(sessionId);
        if(sessionOpt.isEmpty()){
            throw new IllegalArgumentException("Class session not found");
        }
        ClassSession session = sessionOpt.get();

        for(int studentId : studentIds){
            Optional<Attendance> existing = attendanceDAO.findBySessionAndStudent(sessionId, studentId);
            if(existing.isEmpty()){
                AttendanceStatus finalStatus = determineFinalStatus(session, studentId, status);
                Attendance attendance = Attendance.builder()
                    .attendanceId(0)
                    .session(session)
                    .student(new Student(studentId, null, null, null, null, null, null, null, null))
                    .status(finalStatus)
                    .recordedBy(recordedBy)
                    .build();
                attendanceDAO.save(attendance);
            }
        }
    }
}
