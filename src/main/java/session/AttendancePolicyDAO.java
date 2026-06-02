package session;

import application.DatabaseConnection;
import course.Course;
import course.Program;
import course.Semester;
import course.YearLevel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AttendancePolicyDAO {

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            ps.setObject(i + 1, params[i]);
        }
    }

    public Optional<AttendancePolicy> findById(int id){
        String sql = baseQuery() + " WHERE ap.policyId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding attendance policy", e);
        }
        return Optional.empty();
    }

    public List<AttendancePolicy> findAll(){
        String sql = baseQuery();
        List<AttendancePolicy> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(map(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding all attendance policies", e);
        }
        return list;
    }

    public AttendancePolicy save(AttendancePolicy entity){
        String sql = """
            INSERT INTO AttendancePolicy
            (courseId, lateThresholdMinutes, latesEqualToAbsent, absentsEqualToDropped, isActive)
            VALUES (?, ?, ?, ?, ?)
            """;
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(ps,
                entity.course().courseId(),
                entity.lateThresholdMinutes(),
                entity.latesEqualToAbsent(),
                entity.absentsEqualToDropped(),
                entity.active()
            );
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return AttendancePolicy.builder()
                        .policyId(rs.getInt(1))
                        .course(entity.course())
                        .lateThresholdMinutes(entity.lateThresholdMinutes())
                        .latesEqualToAbsent(entity.latesEqualToAbsent())
                        .absentsEqualToDropped(entity.absentsEqualToDropped())
                        .active(entity.active())
                        .build();
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error saving attendance policy", e);
        }
        throw new RuntimeException("Failed to retrieve generated key");
    }

    public boolean update(AttendancePolicy entity){
        String sql = """
            UPDATE AttendancePolicy
            SET courseId = ?, lateThresholdMinutes = ?, latesEqualToAbsent = ?,
                absentsEqualToDropped = ?, isActive = ?
            WHERE policyId = ?
            """;
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps,
                entity.course().courseId(),
                entity.lateThresholdMinutes(),
                entity.latesEqualToAbsent(),
                entity.absentsEqualToDropped(),
                entity.active(),
                entity.policyId()
            );
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error updating attendance policy", e);
        }
    }

    public boolean deleteById(int id){
        String sql = "DELETE FROM AttendancePolicy WHERE policyId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error deleting attendance policy", e);
        }
    }

    public boolean existsById(int id){
        String sql = "SELECT 1 FROM AttendancePolicy WHERE policyId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking attendance policy existence", e);
        }
    }

    public Optional<AttendancePolicy> findByCourseId(int courseId){
        String sql = baseQuery() + " WHERE ap.courseId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, courseId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding attendance policy by course", e);
        }
        return Optional.empty();
    }

    public List<AttendancePolicy> findByActive(boolean active){
        String sql = baseQuery() + " WHERE ap.isActive = ?";
        List<AttendancePolicy> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, active);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding attendance policies by active status", e);
        }
        return list;
    }

    private String baseQuery(){
        return """
            SELECT
                ap.policyId, ap.lateThresholdMinutes, ap.latesEqualToAbsent,
                ap.absentsEqualToDropped, ap.isActive,
                ap.courseId, c.courseCode, c.courseName, c.units, c.yearLevelId AS cYearLevelId,
                c.programId AS cProgramId, cp.programId AS cpProgramId, cp.programName AS cProgramName,
                c.semesterId, csem.semesterId AS csemSemesterId, csem.semesterName, csem.schoolYear, csem.startDate, csem.endDate
            FROM AttendancePolicy ap
            JOIN Course c ON ap.courseId = c.courseId
            JOIN Program cp ON c.programId = cp.programId
            JOIN Semester csem ON c.semesterId = csem.semesterId
            """;
    }

    private AttendancePolicy map(ResultSet rs) throws SQLException{
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

        return AttendancePolicy.builder()
            .policyId(rs.getInt("policyId"))
            .course(course)
            .lateThresholdMinutes(rs.getInt("lateThresholdMinutes"))
            .latesEqualToAbsent(rs.getInt("latesEqualToAbsent"))
            .absentsEqualToDropped(rs.getInt("absentsEqualToDropped"))
            .active(rs.getBoolean("isActive"))
            .build();
    }
}
