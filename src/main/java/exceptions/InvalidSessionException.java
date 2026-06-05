package exceptions;

public class InvalidSessionException extends AttendanceSystemException {

    private final int sessionId;

    public InvalidSessionException(String message, int sessionId) {
        super(message);
        this.sessionId = sessionId;
    }

    public InvalidSessionException(String message) {
        super(message);
        this.sessionId = -1;
    }

    public int getSessionId() {
        return sessionId;
    }

    @Override
    public String toString() {
        return "InvalidSessionException{sessionId=" + sessionId + ", message='" + getMessage() + "'}";
    }
}
