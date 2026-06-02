package user;

import application.DatabaseConnection;
import course.Program;
import course.Section;
import course.Semester;
import course.YearLevel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfessorSectionDAO {

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            ps.setObject(i + 1, params[i]);
        }
    }

    public Optional<ProfessorSection> findById(int id){
        String sql = baseQuery() + " WHERE ps.professorSectionId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding professor section", e);
        }
        return Optional.empty();
    }

    public List<ProfessorSection> findAll(){
        String sql = baseQuery();
        List<ProfessorSection> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(map(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding all professor sections", e);
        }
        return list;
    }

    public ProfessorSection save(ProfessorSection entity){
        String sql = "INSERT INTO ProfessorSection (professorId, sectionId, semesterId, isProfessorRecording) VALUES (?, ?, ?, ?)";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(ps,
                entity.professor().professorId(),
                entity.section().sectionId(),
                entity.semester().semesterId(),
                entity.professorRecording()
            );
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return ProfessorSection.builder()
                        .professorSectionId(rs.getInt(1))
                        .professor(entity.professor())
                        .section(entity.section())
                        .semester(entity.semester())
                        .professorRecording(entity.professorRecording())
                        .build();
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error saving professor section", e);
        }
        throw new RuntimeException("Failed to retrieve generated key");
    }

    public boolean update(ProfessorSection entity){
        String sql = "UPDATE ProfessorSection SET professorId = ?, sectionId = ?, semesterId = ?, isProfessorRecording = ? WHERE professorSectionId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps,
                entity.professor().professorId(),
                entity.section().sectionId(),
                entity.semester().semesterId(),
                entity.professorRecording(),
                entity.professorSectionId()
            );
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error updating professor section", e);
        }
    }

    public boolean deleteById(int id){
        String sql = "DELETE FROM ProfessorSection WHERE professorSectionId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error deleting professor section", e);
        }
    }

    public boolean existsById(int id){
        String sql = "SELECT 1 FROM ProfessorSection WHERE professorSectionId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking professor section existence", e);
        }
    }

    public List<ProfessorSection> findByProfessorId(int professorId){
        String sql = baseQuery() + " WHERE ps.professorId = ?";
        List<ProfessorSection> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, professorId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding professor sections by professor", e);
        }
        return list;
    }

    public List<ProfessorSection> findBySectionId(int sectionId){
        String sql = baseQuery() + " WHERE ps.sectionId = ?";
        List<ProfessorSection> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, sectionId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding professor sections by section", e);
        }
        return list;
    }

    private String baseQuery(){
        return """
            SELECT
                ps.professorSectionId, ps.isProfessorRecording,
                ps.professorId, p.firstName, p.middleName, p.lastName, p.professorTypeId,
                p.userId, pu.userName, pu.userPassword, pu.roleId,
                ps.sectionId, s.programId AS sProgramId, s.yearLevelId AS sYearLevelId, s.sectionCode,
                sp.programId AS spProgramId, sp.programName AS sProgramName,
                ps.semesterId, sem.semesterId AS semSemesterId, sem.semesterName, sem.schoolYear, sem.startDate, sem.endDate
            FROM ProfessorSection ps
            JOIN Professor p ON ps.professorId = p.professorId
            JOIN User pu ON p.userId = pu.userId
            JOIN Section s ON ps.sectionId = s.sectionId
            JOIN Program sp ON s.programId = sp.programId
            JOIN Semester sem ON ps.semesterId = sem.semesterId
            """;
    }

    private ProfessorSection map(ResultSet rs) throws SQLException{
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

        Program secProgram = new Program(rs.getInt("spProgramId"), rs.getString("sProgramName"));
        Section section = new Section(
            rs.getInt("sectionId"),
            secProgram,
            YearLevel.fromId(rs.getInt("sYearLevelId")),
            rs.getString("sectionCode")
        );

        Semester semester = new Semester(
            rs.getInt("semSemesterId"),
            rs.getString("semesterName"),
            rs.getString("schoolYear"),
            rs.getDate("startDate").toLocalDate(),
            rs.getDate("endDate").toLocalDate()
        );

        return ProfessorSection.builder()
            .professorSectionId(rs.getInt("professorSectionId"))
            .professor(professor)
            .section(section)
            .semester(semester)
            .professorRecording(rs.getBoolean("isProfessorRecording"))
            .build();
    }
}
