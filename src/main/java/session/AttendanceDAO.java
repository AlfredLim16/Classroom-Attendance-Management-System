package session;

import application.DatabaseConnection;
import course.Course;
import course.Program;
import course.Section;
import course.Semester;
import course.YearLevel;
import event.SchoolEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import user.Professor;
import user.ProfessorType;
import user.Role;
import user.Student;
import user.User;

public class AttendanceDAO {

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            ps.setObject(i + 1, params[i]);
        }
    }

    public Optional<Attendance> findById(int id){
        String sql = baseQuery() + " WHERE a.attendanceId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding attendance", e);
        }
        return Optional.empty();
    }

    public List<Attendance> findAll(){
        String sql = baseQuery();
        List<Attendance> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(map(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding all attendances", e);
        }
        return list;
    }

    public Attendance save(Attendance entity){
        String sql = "INSERT INTO Attendance (sessionId, studentId, statusId, recordedByUserId) VALUES (?, ?, ?, ?)";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(ps,
                entity.session().sessionId(),
                entity.student().studentId(),
                entity.status().getStatusId(),
                entity.recordedBy().userId()
            );
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return Attendance.builder()
                        .attendanceId(rs.getInt(1))
                        .session(entity.session())
                        .student(entity.student())
                        .status(entity.status())
                        .recordedBy(entity.recordedBy())
                        .build();
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error saving attendance", e);
        }
        throw new RuntimeException("Failed to retrieve generated key");
    }

    public boolean update(Attendance entity){
        String sql = "UPDATE Attendance SET sessionId = ?, studentId = ?, statusId = ?, recordedByUserId = ? WHERE attendanceId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps,
                entity.session().sessionId(),
                entity.student().studentId(),
                entity.status().getStatusId(),
                entity.recordedBy().userId(),
                entity.attendanceId()
            );
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error updating attendance", e);
        }
    }

    public boolean deleteById(int id){
        String sql = "DELETE FROM Attendance WHERE attendanceId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error deleting attendance", e);
        }
    }

    public boolean existsById(int id){
        String sql = "SELECT 1 FROM Attendance WHERE attendanceId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking attendance existence", e);
        }
    }

    public List<Attendance> findBySessionId(int sessionId){
        String sql = baseQuery() + " WHERE a.sessionId = ?";
        List<Attendance> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, sessionId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding attendances by session", e);
        }
        return list;
    }

    public List<Attendance> findByStudentId(int studentId){
        String sql = baseQuery() + " WHERE a.studentId = ?";
        List<Attendance> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, studentId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding attendances by student", e);
        }
        return list;
    }

    public Optional<Attendance> findBySessionAndStudent(int sessionId, int studentId){
        String sql = baseQuery() + " WHERE a.sessionId = ? AND a.studentId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, sessionId, studentId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding attendance by session and student", e);
        }
        return Optional.empty();
    }

    private String baseQuery(){
        return """
            SELECT
                a.attendanceId, a.statusId,
                a.sessionId, cs.sessionDate, cs.startTime, cs.endTime, cs.contextId,
                cs.courseId, c.courseCode, c.courseName, c.units, c.yearLevelId AS cYearLevelId,
                c.programId AS cProgramId, cp.programId AS cpProgramId, cp.programName AS cProgramName,
                c.semesterId, csem.semesterId AS csemSemesterId, csem.semesterName, csem.schoolYear, csem.startDate AS csemStart, csem.endDate AS csemEnd,
                cs.sectionId, s.programId AS sProgramId, s.yearLevelId AS sYearLevelId, s.sectionCode,
                sp.programId AS spProgramId, sp.programName AS sProgramName,
                cs.professorId, p.firstName, p.middleName, p.lastName, p.professorTypeId,
                p.userId, pu.userName, pu.userPassword, pu.roleId,
                cs.eventId, e.eventName, e.eventDate,
                a.studentId, st.studentNumber, st.firstName AS stFirstName, st.middleName AS stMiddleName, st.lastName AS stLastName,
                st.userId AS stUserId, stu.userName AS stuUserName, stu.userPassword AS stuUserPassword, stu.roleId AS stuRoleId,
                st.programId AS stProgramId, stp.programName AS stpProgramName,
                st.yearLevelId AS stYearLevelId,
                st.sectionId AS stSectionId, stsec.programId AS stsecProgramId, stsec.yearLevelId AS stsecYearLevelId, stsec.sectionCode AS stsecSectionCode,
                stsecPr.programName AS stsecProgramName,
                a.recordedByUserId, rb.userName AS rbUserName, rb.userPassword AS rbUserPassword, rb.roleId AS rbRoleId
            FROM Attendance a
            JOIN ClassSession cs ON a.sessionId = cs.sessionId
            JOIN Course c ON cs.courseId = c.courseId
            JOIN Program cp ON c.programId = cp.programId
            JOIN Semester csem ON c.semesterId = csem.semesterId
            JOIN Section s ON cs.sectionId = s.sectionId
            JOIN Program sp ON s.programId = sp.programId
            JOIN Professor p ON cs.professorId = p.professorId
            JOIN User pu ON p.userId = pu.userId
            LEFT JOIN SchoolEvent e ON cs.eventId = e.eventId
            JOIN Student st ON a.studentId = st.studentId
            JOIN User stu ON st.userId = stu.userId
            JOIN Program stp ON st.programId = stp.programId
            JOIN Section stsec ON st.sectionId = stsec.sectionId
            JOIN Program stsecPr ON stsec.programId = stsecPr.programId
            JOIN User rb ON a.recordedByUserId = rb.userId
            """;
    }

    private Attendance map(ResultSet rs) throws SQLException{
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

        ClassSession session = ClassSession.builder()
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

        User stUser = new User(
            rs.getInt("stUserId"),
            rs.getString("stuUserName"),
            rs.getString("stuUserPassword"),
            Role.fromId(rs.getInt("stuRoleId"))
        );
        Program stProgram = new Program(rs.getInt("stProgramId"), rs.getString("stpProgramName"));
        Program stsecProgram = new Program(rs.getInt("stsecProgramId"), rs.getString("stsecProgramName"));
        Section stSection = new Section(
            rs.getInt("stSectionId"),
            stsecProgram,
            YearLevel.fromId(rs.getInt("stsecYearLevelId")),
            rs.getString("stsecSectionCode")
        );
        Student student = Student.builder()
            .studentId(rs.getInt("studentId"))
            .user(stUser)
            .studentNumber(rs.getString("studentNumber"))
            .firstName(rs.getString("stFirstName"))
            .middleName(rs.getString("stMiddleName"))
            .lastName(rs.getString("stLastName"))
            .program(stProgram)
            .yearLevel(YearLevel.fromId(rs.getInt("stYearLevelId")))
            .section(stSection)
            .build();

        User recordedBy = new User(
            rs.getInt("recordedByUserId"),
            rs.getString("rbUserName"),
            rs.getString("rbUserPassword"),
            Role.fromId(rs.getInt("rbRoleId"))
        );

        return Attendance.builder()
            .attendanceId(rs.getInt("attendanceId"))
            .session(session)
            .student(student)
            .status(AttendanceStatus.fromId(rs.getInt("statusId")))
            .recordedBy(recordedBy)
            .build();
    }
}
