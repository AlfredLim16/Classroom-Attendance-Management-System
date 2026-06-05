package exceptions;

public class ValidationException extends AttendanceSystemException {

    private final String field;

    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }

    @Override
    public String toString() {
        return "ValidationException{field='" + field + "', message='" + getMessage() + "'}";
    }
}
