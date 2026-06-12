package services;

import core.*;
import dao.*;
import exceptions.DuplicateEntryException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import junction.ClassSession;
import lookup.ContextType;

public class ClassSessionManagementService {

    private final ClassSessionDAO sessionDAO;

    public ClassSessionManagementService(){
        ProgramDAO programDAO = new ProgramDAOImpl();
        SemesterDAO semesterDAO = new SemesterDAOImpl();
        SchoolEventDAO schoolEventDAO = new SchoolEventDAOImpl();
        SectionDAO sectionDAO = new SectionDAOImpl(programDAO);
        CourseDAO courseDAO = new CourseDAOImpl(programDAO, semesterDAO);
        UserDAO userDAO = new UserDAOImpl();
        ProfessorDAO professorDAO = new ProfessorDAOImpl(userDAO);
        this.sessionDAO = new ClassSessionDAOImpl(courseDAO, sectionDAO, professorDAO, schoolEventDAO);
    }

    public ClassSession createClassroomSession(Course course, Section section, Professor professor, LocalDate date, LocalTime start, LocalTime end){
        try{
            ClassSession session = ClassSession.builder()
                .course(course).section(section).professor(professor)
                .sessionDate(date).startTime(start).endTime(end)
                .contextType(ContextType.CLASSROOM).event(null).build();
            sessionDAO.insert(session);
            return session;
        }catch(SQLException | DuplicateEntryException e){
            System.err.println("[ClassSessionManagementService] createClassroomSession: " + e.getMessage());
            return null;
        }
    }

    public ClassSession createSchoolEventSession(Course course, Section section, Professor professor, LocalDate date, LocalTime start, LocalTime end, SchoolEvent event){
        try{
            ClassSession session = ClassSession.builder()
                .course(course).section(section).professor(professor)
                .sessionDate(date).startTime(start).endTime(end)
                .contextType(ContextType.SCHOOL_EVENT).event(event).build();
            sessionDAO.insert(session);
            return session;
        }catch(SQLException | DuplicateEntryException e){
            System.err.println("[ClassSessionManagementService] createSchoolEventSession: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteSession(int sessionId){
        try{
            sessionDAO.delete(sessionId);
            return true;
        }catch(SQLException | exceptions.NotFoundException e){
            System.err.println("[ClassSessionManagementService] deleteSession: " + e.getMessage());
            return false;
        }
    }
}
