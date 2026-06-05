package junction;

import core.Student;
import core.User;
import lookup.AttendanceStatus;
import validations.AttendanceValidator;
import java.util.Objects;

public record Attendance(int attendanceId, ClassSession session, Student student, AttendanceStatus status, User recordedBy) {

    public Attendance {
        AttendanceValidator.validateAttendance(session, student, status, recordedBy);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Attendance that = (Attendance) object;
        return attendanceId == that.attendanceId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(attendanceId);
    }

    @Override
    public String toString() {
        return "Attendance{id=" + attendanceId + ", student=" + (student != null ? student.studentNumber() : "null") + ", status=" + status + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int attendanceId;
        private ClassSession session;
        private Student student;
        private AttendanceStatus status;
        private User recordedBy;

        public Builder attendanceId(int attendanceId) {
            this.attendanceId = attendanceId;
            return this;
        }

        public Builder session(ClassSession session) {
            this.session = session;
            return this;
        }

        public Builder student(Student student) {
            this.student = student;
            return this;
        }

        public Builder status(AttendanceStatus status) {
            this.status = status;
            return this;
        }

        public Builder recordedBy(User recordedBy) {
            this.recordedBy = recordedBy;
            return this;
        }

        public Attendance build() {
            return new Attendance(attendanceId, session, student, status, recordedBy);
        }
    }
}
