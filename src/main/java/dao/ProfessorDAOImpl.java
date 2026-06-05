package dao;

import core.Professor;
import core.User;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import lookup.ProfessorType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfessorDAOImpl implements ProfessorDAO {

    private final UserDAO userDAO;

    public ProfessorDAOImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Professor mapRow(ResultSet rs) throws SQLException {
        try {
            User user = userDAO.findById(rs.getInt("userId"));
            ProfessorType type = ProfessorType.fromId(rs.getInt("professorTypeId"));
            return Professor.builder()
                    .professorId(rs.getInt("professorId"))
                    .user(user)
                    .firstName(rs.getString("firstName"))
                    .middleName(rs.getString("middleName"))
                    .lastName(rs.getString("lastName"))
                    .professorType(type)
                    .build();
        } catch (ValidationException e) {
            throw new SQLException("Invalid professorTypeId in database for professorId=" + rs.getInt("professorId") + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void insert(Professor professor) throws SQLException, DuplicateEntryException {
        String sql = "INSERT INTO Professor (userId, firstName, middleName, lastName, professorTypeId) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, professor.user().userId());
            ps.setString(2, professor.firstName());
            ps.setString(3, professor.middleName());
            ps.setString(4, professor.lastName());
            ps.setInt(5, professor.professorType().getProfessorTypeId());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("Professor", "userId", professor.user().userId());
        }
    }

    @Override
    public void update(Professor professor) throws SQLException, NotFoundException {
        String sql = "UPDATE Professor SET userId = ?, firstName = ?, middleName = ?, lastName = ?, professorTypeId = ? WHERE professorId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, professor.user().userId());
            ps.setString(2, professor.firstName());
            ps.setString(3, professor.middleName());
            ps.setString(4, professor.lastName());
            ps.setInt(5, professor.professorType().getProfessorTypeId());
            ps.setInt(6, professor.professorId());
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("Professor", professor.professorId());
            }
        }
    }

    @Override
    public void delete(int professorId) throws SQLException, NotFoundException {
        String sql = "DELETE FROM Professor WHERE professorId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, professorId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("Professor", professorId);
            }
        }
    }

    @Override
    public Professor findById(int professorId) throws SQLException, NotFoundException {
        String sql = "SELECT professorId, userId, firstName, middleName, lastName, professorTypeId FROM Professor WHERE professorId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, professorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("Professor", professorId);
            }
        }
    }

    @Override
    public Professor findByUserId(int userId) throws SQLException, NotFoundException {
        String sql = "SELECT professorId, userId, firstName, middleName, lastName, professorTypeId FROM Professor WHERE userId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("Professor", "userId=" + userId);
            }
        }
    }

    @Override
    public List<Professor> findAll() throws SQLException {
        String sql = "SELECT professorId, userId, firstName, middleName, lastName, professorTypeId FROM Professor ORDER BY lastName, firstName";
        List<Professor> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }
}
