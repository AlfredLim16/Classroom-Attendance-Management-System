package exceptions;

public class AttendanceSystemException extends RuntimeException {

    public AttendanceSystemException(String message) {
        super(message);
    }

    public AttendanceSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
