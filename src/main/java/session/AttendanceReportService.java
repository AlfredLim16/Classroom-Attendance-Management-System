package session;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import user.StudentDAO;

public class AttendanceReportService {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final AttendancePolicyDAO attendancePolicyDAO = new AttendancePolicyDAO();
    private final ClassSessionDAO classSessionDAO = new ClassSessionDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    public Map<String, Object> getStudentAttendanceSummary(int studentId, int courseId){
        List<Attendance> attendances = attendanceDAO.findByStudentId(studentId);
        int present = 0, late = 0, absent = 0, excused = 0;

        for(Attendance a : attendances){
            if(a.session().course().courseId() == courseId){
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
        }

        Optional<AttendancePolicy> policyOpt = attendancePolicyDAO.findByCourseId(courseId);
        boolean atRisk = false;
        String status = "Good Standing";

        if(policyOpt.isPresent()){
            AttendancePolicy policy = policyOpt.get();
            int equivalentAbsences = absent + (late / Math.max(policy.latesEqualToAbsent(), 1));
            if(equivalentAbsences >= policy.absentsEqualToDropped()){
                atRisk = true;
                status = "Dropped Risk";
            }else if(equivalentAbsences >= policy.absentsEqualToDropped() * 0.7){
                atRisk = true;
                status = "Warning";
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("studentId", studentId);
        summary.put("courseId", courseId);
        summary.put("present", present);
        summary.put("late", late);
        summary.put("absent", absent);
        summary.put("excused", excused);
        summary.put("totalSessions", present + late + absent + excused);
        summary.put("atRisk", atRisk);
        summary.put("status", status);
        return summary;
    }

    public Map<String, Object> getSectionAttendanceReport(int sectionId, LocalDate date){
        List<ClassSession> sessions = classSessionDAO.findBySectionId(sectionId);
        int totalSessions = 0;
        int totalAttendances = 0;

        for(ClassSession session : sessions){
            if(session.sessionDate().equals(date)){
                List<Attendance> attendances = attendanceDAO.findBySessionId(session.sessionId());
                totalSessions++;
                totalAttendances += attendances.size();
            }
        }

        Map<String, Object> report = new HashMap<>();
        report.put("sectionId", sectionId);
        report.put("date", date);
        report.put("totalSessions", totalSessions);
        report.put("totalAttendancesRecorded", totalAttendances);
        return report;
    }

    public Map<String, Integer> getDailyAttendanceStats(int sessionId){
        List<Attendance> attendances = attendanceDAO.findBySessionId(sessionId);
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Present", 0);
        stats.put("Late", 0);
        stats.put("Absent", 0);
        stats.put("Excused", 0);

        for(Attendance a : attendances){
            String statusName = a.status().getStatusName();
            stats.put(statusName, stats.getOrDefault(statusName, 0) + 1);
        }
        return stats;
    }
}