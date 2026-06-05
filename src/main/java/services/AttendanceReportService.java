package services;

import dao.*;
import junction.Attendance;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceReportService {

    private final AttendanceDAO attendanceDAO;

    public AttendanceReportService(){
        ProgramDAO programDAO = new ProgramDAOImpl();
        SemesterDAO semesterDAO = new SemesterDAOImpl();
        SchoolEventDAO schoolEventDAO = new SchoolEventDAOImpl();
        SectionDAO sectionDAO = new SectionDAOImpl(programDAO);
        CourseDAO courseDAO = new CourseDAOImpl(programDAO, semesterDAO);
        UserDAO userDAO = new UserDAOImpl();
        StudentDAO studentDAO = new StudentDAOImpl(userDAO, programDAO, sectionDAO);
        ProfessorDAO professorDAO = new ProfessorDAOImpl(userDAO);
        ClassSessionDAO sessionDAO = new ClassSessionDAOImpl(courseDAO, sectionDAO, professorDAO, schoolEventDAO);
        this.attendanceDAO = new AttendanceDAOImpl(sessionDAO, studentDAO, userDAO);
    }

    public Map<String, Integer> getDailyAttendanceStats(int sessionId){
        Map<String, Integer> stats = new HashMap<>();
        stats.put("PRESENT", 0);
        stats.put("LATE", 0);
        stats.put("ABSENT", 0);
        stats.put("EXCUSED", 0);
        stats.put("Present", 0);
        stats.put("Late", 0);
        stats.put("Absent", 0);
        stats.put("Excused", 0);
        try{
            List<Attendance> list = attendanceDAO.findBySession(sessionId);
            for(Attendance a : list){
                String name = a.status().getStatusName();
                stats.merge(name, 1, Integer::sum);
                stats.merge(name.toUpperCase(), 1, Integer::sum);
            }
        }catch(SQLException e){
            System.err.println("[AttendanceReportService] getDailyAttendanceStats: " + e.getMessage());
        }
        return stats;
    }
}
