package dao;

import core.SchoolEvent;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SchoolEventDAOImpl implements SchoolEventDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private SchoolEvent mapRow(ResultSet rs) throws SQLException {
        return new SchoolEvent(
                rs.getInt("eventId"),
                rs.getString("eventName"),
                rs.getDate("eventDate").toLocalDate()
        );
    }

    @Override
    public void insert(SchoolEvent event) throws SQLException, DuplicateEntryException {
        String sql = "INSERT INTO SchoolEvent (eventName, eventDate) VALUES (?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, event.eventName());
            ps.setDate(2, Date.valueOf(event.eventDate()));
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("SchoolEvent", "eventName", event.eventName());
        }
    }

    @Override
    public void update(SchoolEvent event) throws SQLException, NotFoundException {
        String sql = "UPDATE SchoolEvent SET eventName = ?, eventDate = ? WHERE eventId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, event.eventName());
            ps.setDate(2, Date.valueOf(event.eventDate()));
            ps.setInt(3, event.eventId());
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("SchoolEvent", event.eventId());
            }
        }
    }

    @Override
    public void delete(int eventId) throws SQLException, NotFoundException {
        String sql = "DELETE FROM SchoolEvent WHERE eventId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, eventId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("SchoolEvent", eventId);
            }
        }
    }

    @Override
    public SchoolEvent findById(int eventId) throws SQLException, NotFoundException {
        String sql = "SELECT eventId, eventName, eventDate FROM SchoolEvent WHERE eventId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("SchoolEvent", eventId);
            }
        }
    }

    @Override
    public List<SchoolEvent> findAll() throws SQLException {
        String sql = "SELECT eventId, eventName, eventDate FROM SchoolEvent ORDER BY eventDate DESC";
        List<SchoolEvent> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }
}
