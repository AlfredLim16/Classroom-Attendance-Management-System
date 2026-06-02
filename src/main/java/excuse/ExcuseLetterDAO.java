package excuse;

import application.DatabaseConnection;
import course.Course;
import course.Program;
import course.Section;
import course.Semester;
import course.YearLevel;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import user.Role;
import user.Student;
import user.User;

public class ExcuseLetterDAO {

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            ps.setObject(i + 1, params[i]);
        }
    }

    public Optional<ExcuseLetter> findById(int id){
        String sql = baseQuery() + " WHERE e.excuseId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding excuse letter", e);
        }
        return Optional.empty();
    }

    public List<ExcuseLetter> findAll(){
        String sql = baseQuery();
        List<ExcuseLetter> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(map(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding all excuse letters", e);
        }
        return list;
    }

    public ExcuseLetter save(ExcuseLetter entity){
        String sql = """
            INSERT INTO ExcuseLetter
            (studentId, courseId, absentDate, reason, supportingDocumentPath, excuseStatusId, reviewedByUserId, submittedDate, reviewedDate)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(ps,
                entity.student().studentId(),
                entity.course().courseId(),
                Date.valueOf(entity.absentDate()),
                entity.reason(),
                entity.supportingDocumentPath(),
                entity.status().getExcuseStatusId(),
                entity.reviewedBy() != null ? entity.reviewedBy().userId() : null,
                Timestamp.valueOf(entity.submittedDate()),
                entity.reviewedDate() != null ? Timestamp.valueOf(entity.reviewedDate()) : null
            );
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return ExcuseLetter.builder()
                        .excuseId(rs.getInt(1))
                        .student(entity.student())
                        .course(entity.course())
                        .absentDate(entity.absentDate())
                        .reason(entity.reason())
                        .supportingDocumentPath(entity.supportingDocumentPath())
                        .status(entity.status())
                        .reviewedBy(entity.reviewedBy())
                        .submittedDate(entity.submittedDate())
                        .reviewedDate(entity.reviewedDate())
                        .build();
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error saving excuse letter", e);
        }
        throw new RuntimeException("Failed to retrieve generated key");
    }

    public boolean update(ExcuseLetter entity){
        String sql = """
            UPDATE ExcuseLetter
            SET studentId = ?, courseId = ?, absentDate = ?, reason = ?,
                supportingDocumentPath = ?, excuseStatusId = ?, reviewedByUserId = ?,
                submittedDate = ?, reviewedDate = ?
            WHERE excuseId = ?
            """;
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps,
                entity.student().studentId(),
                entity.course().courseId(),
                Date.valueOf(entity.absentDate()),
                entity.reason(),
                entity.supportingDocumentPath(),
                entity.status().getExcuseStatusId(),
                entity.reviewedBy() != null ? entity.reviewedBy().userId() : null,
                Timestamp.valueOf(entity.submittedDate()),
                entity.reviewedDate() != null ? Timestamp.valueOf(entity.reviewedDate()) : null,
                entity.excuseId()
            );
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error updating excuse letter", e);
        }
    }

    public boolean deleteById(int id){
        String sql = "DELETE FROM ExcuseLetter WHERE excuseId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error deleting excuse letter", e);
        }
    }

    public boolean existsById(int id){
        String sql = "SELECT 1 FROM ExcuseLetter WHERE excuseId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking excuse letter existence", e);
        }
    }

    public List<ExcuseLetter> findByStudentId(int studentId){
        String sql = baseQuery() + " WHERE e.studentId = ?";
        List<ExcuseLetter> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, studentId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding excuse letters by student", e);
        }
        return list;
    }

    public List<ExcuseLetter> findByCourseId(int courseId){
        String sql = baseQuery() + " WHERE e.courseId = ?";
        List<ExcuseLetter> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, courseId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding excuse letters by course", e);
        }
        return list;
    }

    public List<ExcuseLetter> findByStatus(ExcuseStatus status){
        String sql = baseQuery() + " WHERE e.excuseStatusId = ?";
        List<ExcuseLetter> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, status.getExcuseStatusId());
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding excuse letters by status", e);
        }
        return list;
    }

    public List<ExcuseLetter> findByReviewedBy(int reviewedByUserId){
        String sql = baseQuery() + " WHERE e.reviewedByUserId = ?";
        List<ExcuseLetter> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, reviewedByUserId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding excuse letters by reviewer", e);
        }
        return list;
    }

    private String baseQuery(){
        return """
            SELECT
                e.excuseId, e.absentDate, e.reason, e.supportingDocumentPath, e.submittedDate, e.reviewedDate,
                e.excuseStatusId,
                e.studentId, st.studentNumber, st.firstName AS stFirstName, st.middleName AS stMiddleName, st.lastName AS stLastName,
                st.userId, stu.userName, stu.userPassword, stu.roleId,
                st.programId AS stProgramId, stp.programName AS stpProgramName,
                st.yearLevelId AS stYearLevelId,
                st.sectionId AS stSectionId, stsec.programId AS stsecProgramId, stsec.yearLevelId AS stsecYearLevelId, stsec.sectionCode AS stsecSectionCode,
                stsecPr.programName AS stsecProgramName,
                e.courseId, c.courseCode, c.courseName, c.units, c.yearLevelId AS cYearLevelId,
                c.programId AS cProgramId, cp.programId AS cpProgramId, cp.programName AS cProgramName,
                c.semesterId, csem.semesterId AS csemSemesterId, csem.semesterName, csem.schoolYear, csem.startDate, csem.endDate,
                e.reviewedByUserId, rb.userName AS rbUserName, rb.userPassword AS rbUserPassword, rb.roleId AS rbRoleId
            FROM ExcuseLetter e
            JOIN Student st ON e.studentId = st.studentId
            JOIN User stu ON st.userId = stu.userId
            JOIN Program stp ON st.programId = stp.programId
            JOIN Section stsec ON st.sectionId = stsec.sectionId
            JOIN Program stsecPr ON stsec.programId = stsecPr.programId
            JOIN Course c ON e.courseId = c.courseId
            JOIN Program cp ON c.programId = cp.programId
            JOIN Semester csem ON c.semesterId = csem.semesterId
            LEFT JOIN User rb ON e.reviewedByUserId = rb.userId
            """;
    }

    private ExcuseLetter map(ResultSet rs) throws SQLException{
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

        User reviewedBy = null;
        int reviewedById = rs.getInt("reviewedByUserId");
        if(!rs.wasNull()){
            reviewedBy = new User(
                reviewedById,
                rs.getString("rbUserName"),
                rs.getString("rbUserPassword"),
                Role.fromId(rs.getInt("rbRoleId"))
            );
        }

        LocalDateTime reviewedDate = null;
        Timestamp rd = rs.getTimestamp("reviewedDate");
        if(rd != null){
            reviewedDate = rd.toLocalDateTime();
        }

        return ExcuseLetter.builder()
            .excuseId(rs.getInt("excuseId"))
            .student(student)
            .course(course)
            .absentDate(rs.getDate("absentDate").toLocalDate())
            .reason(rs.getString("reason"))
            .supportingDocumentPath(rs.getString("supportingDocumentPath"))
            .status(ExcuseStatus.fromId(rs.getInt("excuseStatusId")))
            .reviewedBy(reviewedBy)
            .submittedDate(rs.getTimestamp("submittedDate").toLocalDateTime())
            .reviewedDate(reviewedDate)
            .build();
    }
}
