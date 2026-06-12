package services;

import core.Student;
import core.User;
import dao.*;
import exceptions.AttendancePolicyException;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import junction.Attendance;
import junction.ClassSession;
import lookup.AttendanceStatus;

public class AttendanceRecordingService {

    private final AttendanceDAO attendanceDAO;
    private final ClassSessionDAO sessionDAO;
    private final StudentDAO studentDAO;
    private final AttendanceService attendanceService;

    public AttendanceRecordingService(){
        ProgramDAO programDAO = new ProgramDAOImpl();
        SemesterDAO semesterDAO = new SemesterDAOImpl();
        SchoolEventDAO schoolEventDAO = new SchoolEventDAOImpl();
        SectionDAO sectionDAO = new SectionDAOImpl(programDAO);
        CourseDAO courseDAO = new CourseDAOImpl(programDAO, semesterDAO);
        UserDAO userDAO = new UserDAOImpl();
        StudentDAO studentDAOLocal = new StudentDAOImpl(userDAO, programDAO, sectionDAO);
        ProfessorDAO professorDAO = new ProfessorDAOImpl(userDAO);
        ClassSessionDAO sessionDAOLocal = new ClassSessionDAOImpl(courseDAO, sectionDAO, professorDAO, schoolEventDAO);
        this.attendanceDAO = new AttendanceDAOImpl(sessionDAOLocal, studentDAOLocal, userDAO);
        this.sessionDAO = sessionDAOLocal;
        this.studentDAO = studentDAOLocal;
        this.attendanceService = new AttendanceService();
    }

    public List<Attendance> getAttendancesBySession(int sessionId){
        try{
            return attendanceDAO.findBySession(sessionId);
        }catch(SQLException e){
            System.err.println("[AttendanceRecordingService] getAttendancesBySession: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Attendance> getAttendancesByStudent(int studentId){
        try{
            return attendanceDAO.findByStudent(studentId);
        }catch(SQLException e){
            System.err.println("[AttendanceRecordingService] getAttendancesByStudent: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Optional<Attendance> getAttendanceBySessionAndStudent(int sessionId, int studentId){
        try{
            List<Attendance> list = attendanceDAO.findBySession(sessionId);
            return list.stream().filter(a -> a.student().studentId() == studentId).findFirst();
        }catch(SQLException e){
            return Optional.empty();
        }
    }

    public List<String> bulkRecordAttendance(int sessionId, List<Integer> studentIds, AttendanceStatus status, User recordedBy){
        List<String> droppedStudents = new ArrayList<>();
        try{
            ClassSession session = sessionDAO.findById(sessionId);
            for(int studentId : studentIds){
                try{
                    Student student = studentDAO.findById(studentId);
                    attendanceService.recordAttendance(session, student, status, recordedBy);
                }catch(AttendancePolicyException e){
                    droppedStudents.add(e.getMessage());
                }catch(DuplicateEntryException | NotFoundException e){
                    System.err.println("[AttendanceRecordingService] bulkRecord skip studentId=" + studentId + ": " + e.getMessage());
                }
            }
        }catch(SQLException | NotFoundException e){
            System.err.println("[AttendanceRecordingService] bulkRecordAttendance: " + e.getMessage());
        }
        return droppedStudents;
    }

    public boolean updateAttendance(Attendance attendance){
        try{
            attendanceDAO.update(attendance);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[AttendanceRecordingService] updateAttendance: " + e.getMessage());
            return false;
        }
    }
}
