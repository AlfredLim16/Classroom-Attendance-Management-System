package user;

import application.DatabaseConnection;
import course.Course;
import course.Program;
import course.Semester;
import course.YearLevel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfessorCourseDAO {

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            ps.setObject(i + 1, params[i]);
        }
    }

    public Optional<ProfessorCourse> findById(int id){
        String sql = baseQuery() + " WHERE pc.professorCourseId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding professor course", e);
        }
        return Optional.empty();
    }

    public List<ProfessorCourse> findAll(){
        String sql = baseQuery();
        List<ProfessorCourse> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(map(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding all professor courses", e);
        }
        return list;
    }

    public ProfessorCourse save(ProfessorCourse entity){
        String sql = "INSERT INTO ProfessorCourse (professorId, courseId, semesterId) VALUES (?, ?, ?)";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(ps, entity.professor().professorId(), entity.course().courseId(), entity.semester().semesterId());
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return new ProfessorCourse(rs.getInt(1), entity.professor(), entity.course(), entity.semester());
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error saving professor course", e);
        }
        throw new RuntimeException("Failed to retrieve generated key");
    }

    public boolean update(ProfessorCourse entity){
        String sql = "UPDATE ProfessorCourse SET professorId = ?, courseId = ?, semesterId = ? WHERE professorCourseId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, entity.professor().professorId(), entity.course().courseId(), entity.semester().semesterId(), entity.professorCourseId());
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error updating professor course", e);
        }
    }

    public boolean deleteById(int id){
        String sql = "DELETE FROM ProfessorCourse WHERE professorCourseId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error deleting professor course", e);
        }
    }

    public boolean existsById(int id){
        String sql = "SELECT 1 FROM ProfessorCourse WHERE professorCourseId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking professor course existence", e);
        }
    }

    public List<ProfessorCourse> findByProfessorId(int professorId){
        String sql = baseQuery() + " WHERE pc.professorId = ?";
        List<ProfessorCourse> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, professorId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding professor courses by professor", e);
        }
        return list;
    }

    public List<ProfessorCourse> findByCourseId(int courseId){
        String sql = baseQuery() + " WHERE pc.courseId = ?";
        List<ProfessorCourse> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, courseId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding professor courses by course", e);
        }
        return list;
    }

    private String baseQuery(){
        return """
            SELECT
                pc.professorCourseId, pc.professorId, pc.courseId, pc.semesterId,
                p.firstName, p.middleName, p.lastName, p.professorTypeId,
                p.userId, pu.userName, pu.userPassword, pu.roleId,
                c.courseCode, c.courseName, c.units, c.yearLevelId AS cYearLevelId,
                c.programId AS cProgramId, cp.programId AS cpProgramId, cp.programName AS cpProgramName,
                c.semesterId AS cSemesterId,
                sem.semesterId AS semSemesterId, sem.semesterName, sem.schoolYear, sem.startDate, sem.endDate
            FROM ProfessorCourse pc
            JOIN Professor p ON pc.professorId = p.professorId
            JOIN User pu ON p.userId = pu.userId
            JOIN Course c ON pc.courseId = c.courseId
            JOIN Program cp ON c.programId = cp.programId
            JOIN Semester sem ON pc.semesterId = sem.semesterId
            """;
    }

    private ProfessorCourse map(ResultSet rs) throws SQLException{
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

        return new ProfessorCourse(
            rs.getInt("professorCourseId"),
            professor,
            course,
            semester
        );
    }
}
