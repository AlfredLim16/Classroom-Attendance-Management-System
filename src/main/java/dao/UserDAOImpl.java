package dao;

import core.User;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import lookup.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private User mapRow(ResultSet rs) throws SQLException {
        try {
            return User.builder()
                    .userId(rs.getInt("userId"))
                    .userName(rs.getString("userName"))
                    .userPassword(rs.getString("userPassword"))
                    .role(Role.fromId(rs.getInt("roleId")))
                    .build();
        } catch (ValidationException e) {
            throw new SQLException("Invalid roleId in database for userId=" + rs.getInt("userId") + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void insert(User user) throws SQLException, DuplicateEntryException {
        String sql = "INSERT INTO User (userName, userPassword, roleId) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, user.userName());
            ps.setString(2, user.userPassword());
            ps.setInt(3, user.role().getRoleId());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("User", "userName", user.userName());
        }
    }

    @Override
    public void update(User user) throws SQLException, NotFoundException {
        String sql = "UPDATE User SET userName = ?, userPassword = ?, roleId = ? WHERE userId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, user.userName());
            ps.setString(2, user.userPassword());
            ps.setInt(3, user.role().getRoleId());
            ps.setInt(4, user.userId());
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("User", user.userId());
            }
        }
    }

    @Override
    public void delete(int userId) throws SQLException, NotFoundException {
        String sql = "DELETE FROM User WHERE userId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("User", userId);
            }
        }
    }

    @Override
    public User findById(int userId) throws SQLException, NotFoundException {
        String sql = "SELECT userId, userName, userPassword, roleId FROM User WHERE userId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("User", userId);
            }
        }
    }

    @Override
    public User findByUsername(String userName) throws SQLException, NotFoundException {
        String sql = "SELECT userId, userName, userPassword, roleId FROM User WHERE userName = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("User", userName);
            }
        }
    }

    @Override
    public List<User> findAll() throws SQLException {
        String sql = "SELECT userId, userName, userPassword, roleId FROM User ORDER BY userName";
        List<User> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }
}
