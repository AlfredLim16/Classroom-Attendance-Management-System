package course;

import application.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProgramDAO {

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            ps.setObject(i + 1, params[i]);
        }
    }

    public Optional<Program> findById(int id){
        String sql = "SELECT programId, programName FROM Program WHERE programId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding program", e);
        }
        return Optional.empty();
    }

    public List<Program> findAll(){
        String sql = "SELECT programId, programName FROM Program";
        List<Program> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(map(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding all programs", e);
        }
        return list;
    }

    public Program save(Program entity){
        String sql = "INSERT INTO Program (programName) VALUES (?)";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(ps, entity.programName());
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return new Program(rs.getInt(1), entity.programName());
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error saving program", e);
        }
        throw new RuntimeException("Failed to retrieve generated key");
    }

    public boolean update(Program entity){
        String sql = "UPDATE Program SET programName = ? WHERE programId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, entity.programName(), entity.programId());
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error updating program", e);
        }
    }

    public boolean deleteById(int id){
        String sql = "DELETE FROM Program WHERE programId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error deleting program", e);
        }
    }

    public boolean existsById(int id){
        String sql = "SELECT 1 FROM Program WHERE programId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking program existence", e);
        }
    }

    private Program map(ResultSet rs) throws SQLException{
        return new Program(rs.getInt("programId"), rs.getString("programName"));
    }
}
