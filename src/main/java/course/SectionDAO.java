package course;

import application.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SectionDAO {

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            ps.setObject(i + 1, params[i]);
        }
    }

    public Optional<Section> findById(int id){
        String sql = """
            SELECT s.sectionId, s.programId, s.yearLevelId, s.sectionCode,
                   p.programId AS pProgramId, p.programName
            FROM Section s
            JOIN Program p ON s.programId = p.programId
            WHERE s.sectionId = ?
            """;
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding section", e);
        }
        return Optional.empty();
    }

    public List<Section> findAll(){
        String sql = """
            SELECT s.sectionId, s.programId, s.yearLevelId, s.sectionCode,
                   p.programId AS pProgramId, p.programName
            FROM Section s
            JOIN Program p ON s.programId = p.programId
            """;
        List<Section> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(map(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding all sections", e);
        }
        return list;
    }

    public Section save(Section entity){
        String sql = "INSERT INTO Section (programId, yearLevelId, sectionCode) VALUES (?, ?, ?)";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(ps, entity.program().programId(), entity.yearLevel().getYearLevelId(), entity.sectionCode());
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return new Section(rs.getInt(1), entity.program(), entity.yearLevel(), entity.sectionCode());
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error saving section", e);
        }
        throw new RuntimeException("Failed to retrieve generated key");
    }

    public boolean update(Section entity){
        String sql = "UPDATE Section SET programId = ?, yearLevelId = ?, sectionCode = ? WHERE sectionId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, entity.program().programId(), entity.yearLevel().getYearLevelId(), entity.sectionCode(), entity.sectionId());
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error updating section", e);
        }
    }

    public boolean deleteById(int id){
        String sql = "DELETE FROM Section WHERE sectionId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error deleting section", e);
        }
    }

    public boolean existsById(int id){
        String sql = "SELECT 1 FROM Section WHERE sectionId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking section existence", e);
        }
    }

    public List<Section> findByProgramId(int programId){
        String sql = """
            SELECT s.sectionId, s.programId, s.yearLevelId, s.sectionCode,
                   p.programId AS pProgramId, p.programName
            FROM Section s
            JOIN Program p ON s.programId = p.programId
            WHERE s.programId = ?
            """;
        List<Section> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, programId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding sections by program", e);
        }
        return list;
    }

    private Section map(ResultSet rs) throws SQLException{
        Program program = new Program(rs.getInt("pProgramId"), rs.getString("programName"));
        return new Section(
            rs.getInt("sectionId"),
            program,
            YearLevel.fromId(rs.getInt("yearLevelId")),
            rs.getString("sectionCode")
        );
    }
}
