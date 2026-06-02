package session;

import course.Course;
import course.Section;
import course.Semester;
import event.SchoolEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import user.Professor;
import user.ProfessorSectionDAO;
import user.Student;
import user.StudentCourse;
import user.StudentCourseDAO;
import user.User;

public class ClassSessionManagementService {

    private final ClassSessionDAO classSessionDAO = new ClassSessionDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final StudentCourseDAO studentCourseDAO = new StudentCourseDAO();
    private final ProfessorSectionDAO professorSectionDAO = new ProfessorSectionDAO();

    public ClassSession createClassroomSession(Course course, Section section, Professor professor, LocalDate sessionDate, LocalTime startTime, LocalTime endTime){
        ClassSession session = ClassSession.builder()
            .sessionId(0)
            .course(course)
            .section(section)
            .professor(professor)
            .sessionDate(sessionDate)
            .startTime(startTime)
            .endTime(endTime)
            .contextType(ContextType.CLASSROOM)
            .event(null)
            .build();
        return classSessionDAO.save(session);
    }

    public ClassSession createSchoolEventSession(Course course, Section section, Professor professor, LocalDate sessionDate, LocalTime startTime, LocalTime endTime, SchoolEvent event){
        ClassSession session = ClassSession.builder()
            .sessionId(0)
            .course(course)
            .section(section)
            .professor(professor)
            .sessionDate(sessionDate)
            .startTime(startTime)
            .endTime(endTime)
            .contextType(ContextType.SCHOOL_EVENT)
            .event(event)
            .build();
        return classSessionDAO.save(session);
    }

    public void initializeAttendanceSheet(int sessionId, User recordedBy){
        Optional<ClassSession> sessionOpt = classSessionDAO.findById(sessionId);
        if(sessionOpt.isEmpty()){
            throw new IllegalArgumentException("Session not found");
        }

        ClassSession session = sessionOpt.get();
        List<StudentCourse> enrollments = studentCourseDAO.findByCourseId(session.course().courseId());

        for(StudentCourse sc : enrollments){
            Student student = sc.student();
            if(student.section().sectionId() == session.section().sectionId()){
                Optional<Attendance> existing = attendanceDAO.findBySessionAndStudent(sessionId, student.studentId());
                if(existing.isEmpty()){
                    Attendance attendance = Attendance.builder()
                        .attendanceId(0)
                        .session(session)
                        .student(student)
                        .status(AttendanceStatus.ABSENT)
                        .recordedBy(recordedBy)
                        .build();
                    attendanceDAO.save(attendance);
                }
            }
        }
    }

    public boolean canProfessorRecordSession(int professorId, int sectionId, Semester semester){
        var sections = professorSectionDAO.findByProfessorId(professorId);
        return sections.stream()
            .anyMatch(ps -> ps.section().sectionId() == sectionId
            && ps.semester().semesterId() == semester.semesterId()
            && ps.professorRecording());
    }
}
