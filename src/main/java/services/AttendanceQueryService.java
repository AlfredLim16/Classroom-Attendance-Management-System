package services;

import dao.*;
import exceptions.NotFoundException;
import junction.Attendance;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AttendanceQueryService {

    private final AttendanceDAO attendanceDAO;

    public AttendanceQueryService(){
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

    public List<Attendance> getAttendancesBySession(int sessionId){
        try{
            return attendanceDAO.findBySession(sessionId);
        }catch(SQLException e){
            System.err.println("[AttendanceQueryService] getAttendancesBySession: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Attendance> getAttendancesByStudent(int studentId){
        try{
            return attendanceDAO.findByStudent(studentId);
        }catch(SQLException e){
            System.err.println("[AttendanceQueryService] getAttendancesByStudent: " + e.getMessage());
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

    public boolean updateAttendance(Attendance attendance){
        try{
            attendanceDAO.update(attendance);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[AttendanceQueryService] updateAttendance: " + e.getMessage());
            return false;
        }
    }
}
