package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton JDBC connection manager for XAMPP MySQL.
 *
 * Default connection targets:
 *   Host    : localhost
 *   Port    : 3306
 *   Database: classroom_attendance_management
 *   User    : root
 *   Password: (empty — XAMPP default)
 *
 * Change the constants below if your XAMPP setup differs.
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/classroom_attendance_management"
                                         + "?useSSL=false&serverTimezone=Asia/Manila&allowPublicKeyRetrieval=true";
    private static final String USER     = "root";
    private static final String PASSWORD = "";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws SQLException {
        this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Returns the shared DatabaseConnection instance.
     * Creates it on first call; recreates it if the connection has been closed.
     */
    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Returns the underlying JDBC {@link Connection}.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Closes the connection and resets the singleton so the next
     * {@link #getInstance()} call opens a fresh connection.
     */
    public static void closeConnection() {
        if (instance != null) {
            try {
                if (!instance.connection.isClosed()) {
                    instance.connection.close();
                }
            } catch (SQLException e) {
                System.err.println("[DatabaseConnection] Error closing connection: " + e.getMessage());
            } finally {
                instance = null;
            }
        }
    }
}
