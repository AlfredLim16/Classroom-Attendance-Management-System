package dao;

import core.User;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface UserDAO {
    void insert(User user) throws SQLException, DuplicateEntryException;
    void update(User user) throws SQLException, NotFoundException;
    void delete(int userId) throws SQLException, NotFoundException;
    User findById(int userId) throws SQLException, NotFoundException;
    User findByUsername(String userName) throws SQLException, NotFoundException;
    List<User> findAll() throws SQLException;
}
