package user;

import application.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfessorDAO {

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            ps.setObject(i + 1, params[i]);
        }
    }

    public Optional<Professor> findById(int id){
        String sql = """
            SELECT p.professorId, p.firstName, p.middleName, p.lastName, p.professorTypeId,
                   u.userId, u.userName, u.userPassword, u.roleId
            FROM Professor p
            JOIN User u ON p.userId = u.userId
            WHERE p.professorId = ?
            """;
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding professor", e);
        }
        return Optional.empty();
    }

    public List<Professor> findAll(){
        String sql = """
            SELECT p.professorId, p.firstName, p.middleName, p.lastName, p.professorTypeId,
                   u.userId, u.userName, u.userPassword, u.roleId
            FROM Professor p
            JOIN User u ON p.userId = u.userId
            """;
        List<Professor> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(map(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding all professors", e);
        }
        return list;
    }

    public Professor save(Professor entity){
        String sql = "INSERT INTO Professor (userId, firstName, middleName, lastName, professorTypeId) VALUES (?, ?, ?, ?, ?)";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(ps, entity.user().userId(), entity.firstName(), entity.middleName(),
                entity.lastName(), entity.professorType().getProfessorTypeId());
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return new Professor(rs.getInt(1), entity.user(), entity.firstName(),
                        entity.middleName(), entity.lastName(), entity.professorType());
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error saving professor", e);
        }
        throw new RuntimeException("Failed to retrieve generated key");
    }

    public boolean update(Professor entity){
        String sql = "UPDATE Professor SET userId = ?, firstName = ?, middleName = ?, lastName = ?, professorTypeId = ? WHERE professorId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, entity.user().userId(), entity.firstName(), entity.middleName(),
                entity.lastName(), entity.professorType().getProfessorTypeId(), entity.professorId());
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error updating professor", e);
        }
    }

    public boolean deleteById(int id){
        String sql = "DELETE FROM Professor WHERE professorId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error deleting professor", e);
        }
    }

    public boolean existsById(int id){
        String sql = "SELECT 1 FROM Professor WHERE professorId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking professor existence", e);
        }
    }

    public List<Professor> findByProfessorType(ProfessorType type){
        String sql = """
            SELECT p.professorId, p.firstName, p.middleName, p.lastName, p.professorTypeId,
                   u.userId, u.userName, u.userPassword, u.roleId
            FROM Professor p
            JOIN User u ON p.userId = u.userId
            WHERE p.professorTypeId = ?
            """;
        List<Professor> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, type.getProfessorTypeId());
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding professors by type", e);
        }
        return list;
    }

    public Optional<Professor> findByUserId(int userId){
        String sql = """
            SELECT p.professorId, p.firstName, p.middleName, p.lastName, p.professorTypeId,
                   u.userId, u.userName, u.userPassword, u.roleId
            FROM Professor p
            JOIN User u ON p.userId = u.userId
            WHERE p.userId = ?
            """;
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, userId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding professor by user id", e);
        }
        return Optional.empty();
    }

    private Professor map(ResultSet rs) throws SQLException{
        User user = new User(
            rs.getInt("userId"),
            rs.getString("userName"),
            rs.getString("userPassword"),
            Role.fromId(rs.getInt("roleId"))
        );
        return new Professor(
            rs.getInt("professorId"),
            user,
            rs.getString("firstName"),
            rs.getString("middleName"),
            rs.getString("lastName"),
            ProfessorType.fromId(rs.getInt("professorTypeId"))
        );
    }
}
