package session;

import application.DatabaseConnection;
import course.Course;
import course.Program;
import course.Section;
import course.Semester;
import course.YearLevel;
import event.SchoolEvent;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import user.Professor;
import user.ProfessorType;
import user.Role;
import user.User;

public class ClassSessionDAO {

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            ps.setObject(i + 1, params[i]);
        }
    }

    public Optional<ClassSession> findById(int id){
        String sql = baseQuery() + " WHERE cs.sessionId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding class session", e);
        }
        return Optional.empty();
    }

    public List<ClassSession> findAll(){
        String sql = baseQuery();
        List<ClassSession> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(map(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding all class sessions", e);
        }
        return list;
    }

    public ClassSession save(ClassSession entity){
        String sql = """
            INSERT INTO ClassSession
            (courseId, sectionId, professorId, sessionDate, startTime, endTime, contextId, eventId)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(ps,
                entity.course().courseId(),
                entity.section().sectionId(),
                entity.professor().professorId(),
                Date.valueOf(entity.sessionDate()),
                Time.valueOf(entity.startTime()),
                Time.valueOf(entity.endTime()),
                entity.contextType().getContextId(),
                entity.event() != null ? entity.event().eventId() : null
            );
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return ClassSession.builder()
                        .sessionId(rs.getInt(1))
                        .course(entity.course())
                        .section(entity.section())
                        .professor(entity.professor())
                        .sessionDate(entity.sessionDate())
                        .startTime(entity.startTime())
                        .endTime(entity.endTime())
                        .contextType(entity.contextType())
                        .event(entity.event())
                        .build();
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error saving class session", e);
        }
        throw new RuntimeException("Failed to retrieve generated key");
    }

    public boolean update(ClassSession entity){
        String sql = """
            UPDATE ClassSession
            SET courseId = ?, sectionId = ?, professorId = ?, sessionDate = ?,
                startTime = ?, endTime = ?, contextId = ?, eventId = ?
            WHERE sessionId = ?
            """;
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps,
                entity.course().courseId(),
                entity.section().sectionId(),
                entity.professor().professorId(),
                Date.valueOf(entity.sessionDate()),
                Time.valueOf(entity.startTime()),
                Time.valueOf(entity.endTime()),
                entity.contextType().getContextId(),
                entity.event() != null ? entity.event().eventId() : null,
                entity.sessionId()
            );
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error updating class session", e);
        }
    }

    public boolean deleteById(int id){
        String sql = "DELETE FROM ClassSession WHERE sessionId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error deleting class session", e);
        }
    }

    public boolean existsById(int id){
        String sql = "SELECT 1 FROM ClassSession WHERE sessionId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking class session existence", e);
        }
    }

    public List<ClassSession> findByCourseId(int courseId){
        String sql = baseQuery() + " WHERE cs.courseId = ?";
        List<ClassSession> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, courseId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding class sessions by course", e);
        }
        return list;
    }

    public List<ClassSession> findBySectionId(int sectionId){
        String sql = baseQuery() + " WHERE cs.sectionId = ?";
        List<ClassSession> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, sectionId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding class sessions by section", e);
        }
        return list;
    }

    public List<ClassSession> findByProfessorId(int professorId){
        String sql = baseQuery() + " WHERE cs.professorId = ?";
        List<ClassSession> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, professorId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding class sessions by professor", e);
        }
        return list;
    }

    public List<ClassSession> findBySessionDate(LocalDate date){
        String sql = baseQuery() + " WHERE cs.sessionDate = ?";
        List<ClassSession> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, Date.valueOf(date));
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding class sessions by date", e);
        }
        return list;
    }

    private String baseQuery(){
        return """
            SELECT
                cs.sessionId, cs.sessionDate, cs.startTime, cs.endTime, cs.contextId,
                cs.courseId, c.courseCode, c.courseName, c.units, c.yearLevelId AS cYearLevelId,
                c.programId AS cProgramId, cp.programId AS cpProgramId, cp.programName AS cProgramName,
                c.semesterId, csem.semesterId AS csemSemesterId, csem.semesterName, csem.schoolYear, csem.startDate AS csemStart, csem.endDate AS csemEnd,
                cs.sectionId, s.programId AS sProgramId, s.yearLevelId AS sYearLevelId, s.sectionCode,
                sp.programId AS spProgramId, sp.programName AS sProgramName,
                cs.professorId, p.firstName, p.middleName, p.lastName, p.professorTypeId,
                p.userId, pu.userName, pu.userPassword, pu.roleId,
                cs.eventId, e.eventName, e.eventDate
            FROM ClassSession cs
            JOIN Course c ON cs.courseId = c.courseId
            JOIN Program cp ON c.programId = cp.programId
            JOIN Semester csem ON c.semesterId = csem.semesterId
            JOIN Section s ON cs.sectionId = s.sectionId
            JOIN Program sp ON s.programId = sp.programId
            JOIN Professor p ON cs.professorId = p.professorId
            JOIN User pu ON p.userId = pu.userId
            LEFT JOIN SchoolEvent e ON cs.eventId = e.eventId
            """;
    }

    private ClassSession map(ResultSet rs) throws SQLException{
        User pUser = new User(
            rs.getInt("userId"),
            rs.getString("userName"),
            rs.getString("userPassword"),
            Role.fromId(rs.getInt("roleId"))
        );
        Professor professor = new Professor(
            rs.getInt("professorId"),
            pUser,
            rs.getString("firstName"),
            rs.getString("middleName"),
            rs.getString("lastName"),
            ProfessorType.fromId(rs.getInt("professorTypeId"))
        );

        Program cProgram = new Program(rs.getInt("cpProgramId"), rs.getString("cProgramName"));
        Semester cSemester = new Semester(
            rs.getInt("csemSemesterId"),
            rs.getString("semesterName"),
            rs.getString("schoolYear"),
            rs.getDate("csemStart").toLocalDate(),
            rs.getDate("csemEnd").toLocalDate()
        );
        Course course = Course.builder()
            .courseId(rs.getInt("courseId"))
            .program(cProgram)
            .courseCode(rs.getString("courseCode"))
            .courseName(rs.getString("courseName"))
            .units(rs.getByte("units"))
            .semester(cSemester)
            .yearLevel(YearLevel.fromId(rs.getInt("cYearLevelId")))
            .build();

        Program sProgram = new Program(rs.getInt("spProgramId"), rs.getString("sProgramName"));
        Section section = new Section(
            rs.getInt("sectionId"),
            sProgram,
            YearLevel.fromId(rs.getInt("sYearLevelId")),
            rs.getString("sectionCode")
        );

        SchoolEvent event = null;
        int eventId = rs.getInt("eventId");
        if(!rs.wasNull()){
            event = new SchoolEvent(eventId, rs.getString("eventName"), rs.getDate("eventDate").toLocalDate());
        }

        return ClassSession.builder()
            .sessionId(rs.getInt("sessionId"))
            .course(course)
            .section(section)
            .professor(professor)
            .sessionDate(rs.getDate("sessionDate").toLocalDate())
            .startTime(rs.getTime("startTime").toLocalTime())
            .endTime(rs.getTime("endTime").toLocalTime())
            .contextType(ContextType.fromId(rs.getInt("contextId")))
            .event(event)
            .build();
    }
}
