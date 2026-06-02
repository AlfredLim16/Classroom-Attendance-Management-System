package course;

import application.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDAO {

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            ps.setObject(i + 1, params[i]);
        }
    }

    public Optional<Course> findById(int id){
        String sql = """
            SELECT c.courseId, c.courseCode, c.courseName, c.units, c.yearLevelId,
                   c.programId, p.programId AS pProgramId, p.programName,
                   c.semesterId, sem.semesterId AS semSemesterId, sem.semesterName, sem.schoolYear, sem.startDate, sem.endDate
            FROM Course c
            JOIN Program p ON c.programId = p.programId
            JOIN Semester sem ON c.semesterId = sem.semesterId
            WHERE c.courseId = ?
            """;
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding course", e);
        }
        return Optional.empty();
    }

    public List<Course> findAll(){
        String sql = """
            SELECT c.courseId, c.courseCode, c.courseName, c.units, c.yearLevelId,
                   c.programId, p.programId AS pProgramId, p.programName,
                   c.semesterId, sem.semesterId AS semSemesterId, sem.semesterName, sem.schoolYear, sem.startDate, sem.endDate
            FROM Course c
            JOIN Program p ON c.programId = p.programId
            JOIN Semester sem ON c.semesterId = sem.semesterId
            """;
        List<Course> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(map(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding all courses", e);
        }
        return list;
    }

    public Course save(Course entity){
        String sql = "INSERT INTO Course (programId, courseCode, courseName, units, semesterId, yearLevelId) VALUES (?, ?, ?, ?, ?, ?)";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(ps, entity.program().programId(), entity.courseCode(), entity.courseName(),
                entity.units(), entity.semester().semesterId(), entity.yearLevel().getYearLevelId());
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return Course.builder()
                        .courseId(rs.getInt(1))
                        .program(entity.program())
                        .courseCode(entity.courseCode())
                        .courseName(entity.courseName())
                        .units(entity.units())
                        .semester(entity.semester())
                        .yearLevel(entity.yearLevel())
                        .build();
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error saving course", e);
        }
        throw new RuntimeException("Failed to retrieve generated key");
    }

    public boolean update(Course entity){
        String sql = "UPDATE Course SET programId = ?, courseCode = ?, courseName = ?, units = ?, semesterId = ?, yearLevelId = ? WHERE courseId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, entity.program().programId(), entity.courseCode(), entity.courseName(),
                entity.units(), entity.semester().semesterId(), entity.yearLevel().getYearLevelId(),
                entity.courseId());
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error updating course", e);
        }
    }

    public boolean deleteById(int id){
        String sql = "DELETE FROM Course WHERE courseId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error deleting course", e);
        }
    }

    public boolean existsById(int id){
        String sql = "SELECT 1 FROM Course WHERE courseId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking course existence", e);
        }
    }

    public List<Course> findByProgramId(int programId){
        String sql = """
            SELECT c.courseId, c.courseCode, c.courseName, c.units, c.yearLevelId,
                   c.programId, p.programId AS pProgramId, p.programName,
                   c.semesterId, sem.semesterId AS semSemesterId, sem.semesterName, sem.schoolYear, sem.startDate, sem.endDate
            FROM Course c
            JOIN Program p ON c.programId = p.programId
            JOIN Semester sem ON c.semesterId = sem.semesterId
            WHERE c.programId = ?
            """;
        List<Course> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, programId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding courses by program", e);
        }
        return list;
    }

    private Course map(ResultSet rs) throws SQLException{
        Program program = new Program(rs.getInt("pProgramId"), rs.getString("programName"));
        Semester semester = new Semester(
            rs.getInt("semSemesterId"),
            rs.getString("semesterName"),
            rs.getString("schoolYear"),
            rs.getDate("startDate").toLocalDate(),
            rs.getDate("endDate").toLocalDate()
        );
        return Course.builder()
            .courseId(rs.getInt("courseId"))
            .program(program)
            .courseCode(rs.getString("courseCode"))
            .courseName(rs.getString("courseName"))
            .units(rs.getByte("units"))
            .semester(semester)
            .yearLevel(YearLevel.fromId(rs.getInt("yearLevelId")))
            .build();
    }
}
