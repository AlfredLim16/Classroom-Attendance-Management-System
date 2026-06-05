package exceptions;

public class AttendancePolicyException extends AttendanceSystemException {

    private final int studentId;
    private final int courseId;

    public AttendancePolicyException(String message, int studentId, int courseId) {
        super(message);
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    @Override
    public String toString() {
        return "AttendancePolicyException{studentId=" + studentId + ", courseId=" + courseId + ", message='" + getMessage() + "'}";
    }
}
