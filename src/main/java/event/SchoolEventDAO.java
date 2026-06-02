package event;

import application.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SchoolEventDAO {

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            ps.setObject(i + 1, params[i]);
        }
    }

    public Optional<SchoolEvent> findById(int id){
        String sql = "SELECT eventId, eventName, eventDate FROM SchoolEvent WHERE eventId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding school event", e);
        }
        return Optional.empty();
    }

    public List<SchoolEvent> findAll(){
        String sql = "SELECT eventId, eventName, eventDate FROM SchoolEvent";
        List<SchoolEvent> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(map(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding all school events", e);
        }
        return list;
    }

    public SchoolEvent save(SchoolEvent entity){
        String sql = "INSERT INTO SchoolEvent (eventName, eventDate) VALUES (?, ?)";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(ps, entity.eventName(), Date.valueOf(entity.eventDate()));
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return new SchoolEvent(rs.getInt(1), entity.eventName(), entity.eventDate());
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error saving school event", e);
        }
        throw new RuntimeException("Failed to retrieve generated key");
    }

    public boolean update(SchoolEvent entity){
        String sql = "UPDATE SchoolEvent SET eventName = ?, eventDate = ? WHERE eventId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, entity.eventName(), Date.valueOf(entity.eventDate()), entity.eventId());
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error updating school event", e);
        }
    }

    public boolean deleteById(int id){
        String sql = "DELETE FROM SchoolEvent WHERE eventId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error deleting school event", e);
        }
    }

    public boolean existsById(int id){
        String sql = "SELECT 1 FROM SchoolEvent WHERE eventId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking school event existence", e);
        }
    }

    public List<SchoolEvent> findByEventDate(LocalDate date){
        String sql = "SELECT eventId, eventName, eventDate FROM SchoolEvent WHERE eventDate = ?";
        List<SchoolEvent> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, Date.valueOf(date));
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding school events by date", e);
        }
        return list;
    }

    private SchoolEvent map(ResultSet rs) throws SQLException{
        return new SchoolEvent(
            rs.getInt("eventId"),
            rs.getString("eventName"),
            rs.getDate("eventDate").toLocalDate()
        );
    }
}
