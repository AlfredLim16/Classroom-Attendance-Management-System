package session;

import course.Course;
import course.Section;
import event.SchoolEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import user.Professor;

public class ClassSessionService {

    private final ClassSessionDAO classSessionDAO = new ClassSessionDAO();

    public ClassSession createClassSession(Course course, Section section, Professor professor, LocalDate sessionDate, LocalTime startTime, LocalTime endTime, ContextType contextType, SchoolEvent event){
        ClassSession session = ClassSession.builder()
            .sessionId(0)
            .course(course)
            .section(section)
            .professor(professor)
            .sessionDate(sessionDate)
            .startTime(startTime)
            .endTime(endTime)
            .contextType(contextType)
            .event(event)
            .build();
        return classSessionDAO.save(session);
    }

    public Optional<ClassSession> getClassSessionById(int id){
        return classSessionDAO.findById(id);
    }

    public List<ClassSession> getAllClassSessions(){
        return classSessionDAO.findAll();
    }

    public List<ClassSession> getClassSessionsByCourse(int courseId){
        return classSessionDAO.findByCourseId(courseId);
    }

    public List<ClassSession> getClassSessionsBySection(int sectionId){
        return classSessionDAO.findBySectionId(sectionId);
    }

    public List<ClassSession> getClassSessionsByProfessor(int professorId){
        return classSessionDAO.findByProfessorId(professorId);
    }

    public List<ClassSession> getClassSessionsByDate(LocalDate date){
        return classSessionDAO.findBySessionDate(date);
    }

    public boolean updateClassSession(ClassSession session){
        return classSessionDAO.update(session);
    }

    public boolean deleteClassSession(int id){
        return classSessionDAO.deleteById(id);
    }
}
