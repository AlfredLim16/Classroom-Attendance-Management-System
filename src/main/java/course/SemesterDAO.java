package course;

import application.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SemesterDAO {

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            ps.setObject(i + 1, params[i]);
        }
    }

    public Optional<Semester> findById(int id){
        String sql = "SELECT semesterId, semesterName, schoolYear, startDate, endDate FROM Semester WHERE semesterId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding semester", e);
        }
        return Optional.empty();
    }

    public List<Semester> findAll(){
        String sql = "SELECT semesterId, semesterName, schoolYear, startDate, endDate FROM Semester";
        List<Semester> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(map(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding all semesters", e);
        }
        return list;
    }

    public Semester save(Semester entity){
        String sql = "INSERT INTO Semester (semesterName, schoolYear, startDate, endDate) VALUES (?, ?, ?, ?)";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(ps, entity.semesterName(), entity.schoolYear(),
                Date.valueOf(entity.startDate()), Date.valueOf(entity.endDate()));
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return new Semester(rs.getInt(1), entity.semesterName(), entity.schoolYear(),
                        entity.startDate(), entity.endDate());
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error saving semester", e);
        }
        throw new RuntimeException("Failed to retrieve generated key");
    }

    public boolean update(Semester entity){
        String sql = "UPDATE Semester SET semesterName = ?, schoolYear = ?, startDate = ?, endDate = ? WHERE semesterId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, entity.semesterName(), entity.schoolYear(),
                Date.valueOf(entity.startDate()), Date.valueOf(entity.endDate()),
                entity.semesterId());
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error updating semester", e);
        }
    }

    public boolean deleteById(int id){
        String sql = "DELETE FROM Semester WHERE semesterId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error deleting semester", e);
        }
    }

    public boolean existsById(int id){
        String sql = "SELECT 1 FROM Semester WHERE semesterId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking semester existence", e);
        }
    }

    private Semester map(ResultSet rs) throws SQLException{
        return new Semester(
            rs.getInt("semesterId"),
            rs.getString("semesterName"),
            rs.getString("schoolYear"),
            rs.getDate("startDate").toLocalDate(),
            rs.getDate("endDate").toLocalDate()
        );
    }
}
