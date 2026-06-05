package exceptions;

public class UnauthorizedAccessException extends AttendanceSystemException {

    private final String action;
    private final String role;

    public UnauthorizedAccessException(String role, String action) {
        super("Role '" + role + "' is not authorized to perform: " + action);
        this.action = action;
        this.role = role;
    }

    public String getAction() {
        return action;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "UnauthorizedAccessException{role='" + role + "', action='" + action + "'}";
    }
}
