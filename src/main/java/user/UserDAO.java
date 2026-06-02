package user;

import application.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            ps.setObject(i + 1, params[i]);
        }
    }

    public Optional<User> findById(int id){
        String sql = "SELECT userId, userName, userPassword, roleId FROM User WHERE userId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding user by id", e);
        }
        return Optional.empty();
    }

    public List<User> findAll(){
        String sql = "SELECT userId, userName, userPassword, roleId FROM User";
        List<User> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(map(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding all users", e);
        }
        return list;
    }

    public User save(User entity){
        String sql = "INSERT INTO User (userName, userPassword, roleId) VALUES (?, ?, ?)";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(ps, entity.userName(), entity.userPassword(), entity.role().getRoleId());
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    int id = rs.getInt(1);
                    return new User(id, entity.userName(), entity.userPassword(), entity.role());
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error saving user", e);
        }
        throw new RuntimeException("Failed to retrieve generated key");
    }

    public boolean update(User entity){
        String sql = "UPDATE User SET userName = ?, userPassword = ?, roleId = ? WHERE userId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, entity.userName(), entity.userPassword(), entity.role().getRoleId(), entity.userId());
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error updating user", e);
        }
    }

    public boolean deleteById(int id){
        String sql = "DELETE FROM User WHERE userId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error deleting user", e);
        }
    }

    public boolean existsById(int id){
        String sql = "SELECT 1 FROM User WHERE userId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking user existence", e);
        }
    }

    public Optional<User> findByUserName(String userName){
        String sql = "SELECT userId, userName, userPassword, roleId FROM User WHERE userName = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, userName);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding user by username", e);
        }
        return Optional.empty();
    }

    private User map(ResultSet rs) throws SQLException{
        return new User(
            rs.getInt("userId"),
            rs.getString("userName"),
            rs.getString("userPassword"),
            Role.fromId(rs.getInt("roleId"))
        );
    }
}
