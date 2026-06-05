package services;

import core.User;
import dao.*;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import lookup.Role;
import validations.UserValidator;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class UserService {

    private final UserDAO userDAO;

    public UserService(){
        this.userDAO = new UserDAOImpl();
    }

    public User createUser(String username, String password, Role role){
        try{
            UserValidator.validateRawPassword(password);
            User user = User.builder().userName(username).userPassword(password).role(role).build();
            userDAO.insert(user);
            return userDAO.findByUsername(username);
        }catch(SQLException | DuplicateEntryException | NotFoundException e){
            System.err.println("[UserService] createUser: " + e.getMessage());
            return null;
        }
    }

    public boolean updateUser(User user){
        try{
            userDAO.update(user);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[UserService] updateUser: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(int userId){
        try{
            userDAO.delete(userId);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[UserService] deleteUser: " + e.getMessage());
            return false;
        }
    }

    public List<User> getAllUsers(){
        try{
            return userDAO.findAll();
        }catch(SQLException e){
            System.err.println("[UserService] getAllUsers: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
