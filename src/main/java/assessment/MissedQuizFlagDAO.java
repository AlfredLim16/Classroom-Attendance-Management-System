package assessment;

import application.DatabaseConnection;
import course.Course;
import course.Program;
import course.Section;
import course.Semester;
import course.YearLevel;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import user.Professor;
import user.ProfessorType;
import user.Role;
import user.Student;
import user.User;

public class MissedQuizFlagDAO {

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            ps.setObject(i + 1, params[i]);
        }
    }

    public Optional<MissedQuizFlag> findById(int id){
        String sql = baseQuery() + " WHERE f.flagId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding missed quiz flag", e);
        }
        return Optional.empty();
    }

    public List<MissedQuizFlag> findAll(){
        String sql = baseQuery();
        List<MissedQuizFlag> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(map(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding all missed quiz flags", e);
        }
        return list;
    }

    public MissedQuizFlag save(MissedQuizFlag entity){
        String sql = """
            INSERT INTO MissedQuizFlag
            (studentId, quizId, missedQuizStatusId, decisionTypeId, remarks, decisionDate, decidedByProfessorId)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(ps,
                entity.student().studentId(),
                entity.quiz().quizId(),
                entity.status().getMissedQuizStatusId(),
                entity.decisionType().getDecisionTypeId(),
                entity.remarks(),
                entity.decisionDate() != null ? Date.valueOf(entity.decisionDate()) : null,
                entity.decidedBy().professorId()
            );
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return MissedQuizFlag.builder()
                        .flagId(rs.getInt(1))
                        .student(entity.student())
                        .quiz(entity.quiz())
                        .status(entity.status())
                        .decisionType(entity.decisionType())
                        .remarks(entity.remarks())
                        .decisionDate(entity.decisionDate())
                        .decidedBy(entity.decidedBy())
                        .build();
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error saving missed quiz flag", e);
        }
        throw new RuntimeException("Failed to retrieve generated key");
    }

    public boolean update(MissedQuizFlag entity){
        String sql = """
            UPDATE MissedQuizFlag
            SET studentId = ?, quizId = ?, missedQuizStatusId = ?, decisionTypeId = ?,
                remarks = ?, decisionDate = ?, decidedByProfessorId = ?
            WHERE flagId = ?
            """;
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps,
                entity.student().studentId(),
                entity.quiz().quizId(),
                entity.status().getMissedQuizStatusId(),
                entity.decisionType().getDecisionTypeId(),
                entity.remarks(),
                entity.decisionDate() != null ? Date.valueOf(entity.decisionDate()) : null,
                entity.decidedBy().professorId(),
                entity.flagId()
            );
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error updating missed quiz flag", e);
        }
    }

    public boolean deleteById(int id){
        String sql = "DELETE FROM MissedQuizFlag WHERE flagId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error deleting missed quiz flag", e);
        }
    }

    public boolean existsById(int id){
        String sql = "SELECT 1 FROM MissedQuizFlag WHERE flagId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking missed quiz flag existence", e);
        }
    }

    public List<MissedQuizFlag> findByStudentId(int studentId){
        String sql = baseQuery() + " WHERE f.studentId = ?";
        List<MissedQuizFlag> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, studentId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding missed quiz flags by student", e);
        }
        return list;
    }

    public List<MissedQuizFlag> findByQuizId(int quizId){
        String sql = baseQuery() + " WHERE f.quizId = ?";
        List<MissedQuizFlag> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, quizId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding missed quiz flags by quiz", e);
        }
        return list;
    }

    public List<MissedQuizFlag> findByStatus(MissedQuizStatus status){
        String sql = baseQuery() + " WHERE f.missedQuizStatusId = ?";
        List<MissedQuizFlag> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, status.getMissedQuizStatusId());
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding missed quiz flags by status", e);
        }
        return list;
    }

    public List<MissedQuizFlag> findByDecisionType(DecisionType decisionType){
        String sql = baseQuery() + " WHERE f.decisionTypeId = ?";
        List<MissedQuizFlag> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, decisionType.getDecisionTypeId());
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding missed quiz flags by decision type", e);
        }
        return list;
    }

    private String baseQuery(){
        return """
            SELECT
                f.flagId, f.remarks, f.decisionDate,
                f.studentId, st.studentNumber, st.firstName AS stFirstName, st.middleName AS stMiddleName, st.lastName AS stLastName,
                st.userId, stu.userName, stu.userPassword, stu.roleId,
                st.programId AS stProgramId, stp.programName AS stpProgramName,
                st.yearLevelId AS stYearLevelId,
                st.sectionId AS stSectionId, stsec.programId AS stsecProgramId, stsec.yearLevelId AS stsecYearLevelId, stsec.sectionCode AS stsecSectionCode,
                stsecPr.programName AS stsecProgramName,
                f.quizId, q.quizDate, q.quizTypeId,
                q.courseId, c.courseCode, c.courseName, c.units, c.yearLevelId AS cYearLevelId,
                c.programId AS cProgramId, cp.programId AS cpProgramId, cp.programName AS cProgramName,
                c.semesterId, csem.semesterId AS csemSemesterId, csem.semesterName, csem.schoolYear, csem.startDate, csem.endDate,
                f.missedQuizStatusId, f.decisionTypeId,
                f.decidedByProfessorId, p.firstName, p.middleName, p.lastName, p.professorTypeId,
                p.userId AS pUserId, pu.userName AS puUserName, pu.userPassword AS puUserPassword, pu.roleId AS puRoleId
            FROM MissedQuizFlag f
            JOIN Student st ON f.studentId = st.studentId
            JOIN User stu ON st.userId = stu.userId
            JOIN Program stp ON st.programId = stp.programId
            JOIN Section stsec ON st.sectionId = stsec.sectionId
            JOIN Program stsecPr ON stsec.programId = stsecPr.programId
            JOIN QuizLabSchedule q ON f.quizId = q.quizId
            JOIN Course c ON q.courseId = c.courseId
            JOIN Program cp ON c.programId = cp.programId
            JOIN Semester csem ON c.semesterId = csem.semesterId
            JOIN Professor p ON f.decidedByProfessorId = p.professorId
            JOIN User pu ON p.userId = pu.userId
            """;
    }

    private MissedQuizFlag map(ResultSet rs) throws SQLException{
        User stUser = new User(
            rs.getInt("userId"),
            rs.getString("userName"),
            rs.getString("userPassword"),
            Role.fromId(rs.getInt("roleId"))
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

        Program cProgram = new Program(rs.getInt("cpProgramId"), rs.getString("cProgramName"));
        Semester cSemester = new Semester(
            rs.getInt("csemSemesterId"),
            rs.getString("semesterName"),
            rs.getString("schoolYear"),
            rs.getDate("startDate").toLocalDate(),
            rs.getDate("endDate").toLocalDate()
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

        QuizLabSchedule quiz = new QuizLabSchedule(
            rs.getInt("quizId"),
            course,
            rs.getDate("quizDate").toLocalDate(),
            QuizType.fromId(rs.getInt("quizTypeId"))
        );

        User pUser = new User(
            rs.getInt("pUserId"),
            rs.getString("puUserName"),
            rs.getString("puUserPassword"),
            Role.fromId(rs.getInt("puRoleId"))
        );
        Professor professor = new Professor(
            rs.getInt("decidedByProfessorId"),
            pUser,
            rs.getString("firstName"),
            rs.getString("middleName"),
            rs.getString("lastName"),
            ProfessorType.fromId(rs.getInt("professorTypeId"))
        );

        LocalDate decisionDate = null;
        Date dd = rs.getDate("decisionDate");
        if(dd != null){
            decisionDate = dd.toLocalDate();
        }

        return MissedQuizFlag.builder()
            .flagId(rs.getInt("flagId"))
            .student(student)
            .quiz(quiz)
            .status(MissedQuizStatus.fromId(rs.getInt("missedQuizStatusId")))
            .decisionType(DecisionType.fromId(rs.getInt("decisionTypeId")))
            .remarks(rs.getString("remarks"))
            .decisionDate(decisionDate)
            .decidedBy(professor)
            .build();
    }
}
