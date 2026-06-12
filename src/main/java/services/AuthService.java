package services;

import core.User;
import dao.DatabaseConnection;
import dao.UserDAO;
import dao.UserDAOImpl;
import exceptions.NotFoundException;
import exceptions.UnauthorizedAccessException;
import java.sql.SQLException;
import java.util.Optional;
import validations.UserValidator;


public class AuthService {

    private final UserDAO userDAO;
    private User currentUser;

    public AuthService() {
        this.userDAO = new UserDAOImpl();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Optional<User> login(String username, String password) {
        try {
            UserValidator.validateRawPassword(password);
            User user = userDAO.findByUsername(username);
            if (PasswordUtil.verify(password, user.userPassword())) {
                this.currentUser = user;
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (NotFoundException e) {
            return Optional.empty();
        } catch (SQLException e) {
            System.err.println("[AuthService] DB error during login: " + e.getMessage());
            return Optional.empty();
        }
    }

    public void logout() {
        this.currentUser = null;
        DatabaseConnection.closeConnection();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void requireRole(lookup.Role required) {
        if (currentUser == null || currentUser.role() != required) {
            String role = currentUser != null ? currentUser.role().getRoleName() : "unauthenticated";
            throw new UnauthorizedAccessException(role, "access " + required.getRoleName() + " features");
        }
    }
}
