package user;

import application.DatabaseConnection;
import course.Course;
import course.Program;
import course.Section;
import course.Semester;
import course.YearLevel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentCourseDAO {

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            ps.setObject(i + 1, params[i]);
        }
    }

    public Optional<StudentCourse> findById(int id){
        String sql = baseQuery() + " WHERE sc.studentCourseId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding student course", e);
        }
        return Optional.empty();
    }

    public List<StudentCourse> findAll(){
        String sql = baseQuery();
        List<StudentCourse> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(map(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding all student courses", e);
        }
        return list;
    }

    public StudentCourse save(StudentCourse entity){
        String sql = "INSERT INTO StudentCourse (studentId, courseId, semesterId) VALUES (?, ?, ?)";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(ps, entity.student().studentId(), entity.course().courseId(), entity.semester().semesterId());
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return new StudentCourse(rs.getInt(1), entity.student(), entity.course(), entity.semester());
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error saving student course", e);
        }
        throw new RuntimeException("Failed to retrieve generated key");
    }

    public boolean update(StudentCourse entity){
        String sql = "UPDATE StudentCourse SET studentId = ?, courseId = ?, semesterId = ? WHERE studentCourseId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, entity.student().studentId(), entity.course().courseId(), entity.semester().semesterId(), entity.studentCourseId());
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error updating student course", e);
        }
    }

    public boolean deleteById(int id){
        String sql = "DELETE FROM StudentCourse WHERE studentCourseId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error deleting student course", e);
        }
    }

    public boolean existsById(int id){
        String sql = "SELECT 1 FROM StudentCourse WHERE studentCourseId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking student course existence", e);
        }
    }

    public List<StudentCourse> findByStudentId(int studentId){
        String sql = baseQuery() + " WHERE sc.studentId = ?";
        List<StudentCourse> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, studentId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding student courses by student", e);
        }
        return list;
    }

    public List<StudentCourse> findByCourseId(int courseId){
        String sql = baseQuery() + " WHERE sc.courseId = ?";
        List<StudentCourse> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, courseId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding student courses by course", e);
        }
        return list;
    }

    private String baseQuery(){
        return """
            SELECT
                sc.studentCourseId, sc.studentId, sc.courseId, sc.semesterId,
                st.studentNumber, st.firstName AS stFirstName, st.middleName AS stMiddleName, st.lastName AS stLastName,
                st.userId, stu.userName, stu.userPassword, stu.roleId,
                st.programId AS stProgramId, stp.programName AS stProgramName,
                st.yearLevelId AS stYearLevelId,
                st.sectionId AS stSectionId, stsec.programId AS stsecProgramId, stsec.yearLevelId AS stsecYearLevelId, stsec.sectionCode AS stsecSectionCode,
                stsecPr.programName AS stsecProgramName,
                c.courseCode, c.courseName, c.units, c.yearLevelId AS cYearLevelId,
                c.programId AS cProgramId, cp.programId AS cpProgramId, cp.programName AS cpProgramName,
                c.semesterId AS cSemesterId,
                sem.semesterId AS semSemesterId, sem.semesterName, sem.schoolYear, sem.startDate, sem.endDate
            FROM StudentCourse sc
            JOIN Student st ON sc.studentId = st.studentId
            JOIN User stu ON st.userId = stu.userId
            JOIN Program stp ON st.programId = stp.programId
            JOIN Section stsec ON st.sectionId = stsec.sectionId
            JOIN Program stsecPr ON stsec.programId = stsecPr.programId
            JOIN Course c ON sc.courseId = c.courseId
            JOIN Program cp ON c.programId = cp.programId
            JOIN Semester sem ON sc.semesterId = sem.semesterId
            """;
    }

    private StudentCourse map(ResultSet rs) throws SQLException{
        User stUser = new User(
            rs.getInt("userId"),
            rs.getString("userName"),
            rs.getString("userPassword"),
            Role.fromId(rs.getInt("roleId"))
        );
        Program stProgram = new Program(rs.getInt("stProgramId"), rs.getString("stProgramName"));
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

        Program cProgram = new Program(rs.getInt("cpProgramId"), rs.getString("cpProgramName"));
        Semester semester = new Semester(
            rs.getInt("semSemesterId"),
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
            .semester(semester)
            .yearLevel(YearLevel.fromId(rs.getInt("cYearLevelId")))
            .build();

        return new StudentCourse(
            rs.getInt("studentCourseId"),
            student,
            course,
            semester
        );
    }
}
