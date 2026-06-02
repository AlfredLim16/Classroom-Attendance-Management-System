package session;

import java.util.List;
import java.util.Optional;
import user.Student;
import user.User;

public class AttendanceService {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    public Attendance recordAttendance(ClassSession session, Student student, AttendanceStatus status, User recordedBy){
        Attendance attendance = Attendance.builder()
            .attendanceId(0)
            .session(session)
            .student(student)
            .status(status)
            .recordedBy(recordedBy)
            .build();
        return attendanceDAO.save(attendance);
    }

    public Optional<Attendance> getAttendanceById(int id){
        return attendanceDAO.findById(id);
    }

    public List<Attendance> getAllAttendances(){
        return attendanceDAO.findAll();
    }

    public List<Attendance> getAttendancesBySession(int sessionId){
        return attendanceDAO.findBySessionId(sessionId);
    }

    public List<Attendance> getAttendancesByStudent(int studentId){
        return attendanceDAO.findByStudentId(studentId);
    }

    public Optional<Attendance> getAttendanceBySessionAndStudent(int sessionId, int studentId){
        return attendanceDAO.findBySessionAndStudent(sessionId, studentId);
    }

    public boolean updateAttendance(Attendance attendance){
        return attendanceDAO.update(attendance);
    }

    public boolean deleteAttendance(int id){
        return attendanceDAO.deleteById(id);
    }
}
