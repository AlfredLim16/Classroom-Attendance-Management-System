package junction;

import core.Course;
import validations.AttendanceValidator;
import java.util.Objects;

public record AttendancePolicy(
    int policyId, Course course, int lateThresholdMinutes, int latesEqualToAbsent, int absentsEqualToDropped, boolean isActive) {

    public AttendancePolicy {
        AttendanceValidator.validatePolicy(course, lateThresholdMinutes, latesEqualToAbsent, absentsEqualToDropped);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        AttendancePolicy that = (AttendancePolicy) object;
        return policyId == that.policyId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(policyId);
    }

    @Override
    public String toString() {
        return "AttendancePolicy{id=" + policyId + ", course=" + (course != null ? course.courseCode() : "null") + ", lateThreshold=" + lateThresholdMinutes + "min, active=" + isActive + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int policyId;
        private Course course;
        private int lateThresholdMinutes;
        private int latesEqualToAbsent;
        private int absentsEqualToDropped;
        private boolean isActive;

        public Builder policyId(int policyId) {
            this.policyId = policyId;
            return this;
        }

        public Builder course(Course course) {
            this.course = course;
            return this;
        }

        public Builder lateThresholdMinutes(int lateThresholdMinutes) {
            this.lateThresholdMinutes = lateThresholdMinutes;
            return this;
        }

        public Builder latesEqualToAbsent(int latesEqualToAbsent) {
            this.latesEqualToAbsent = latesEqualToAbsent;
            return this;
        }

        public Builder absentsEqualToDropped(int absentsEqualToDropped) {
            this.absentsEqualToDropped = absentsEqualToDropped;
            return this;
        }

        public Builder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public AttendancePolicy build() {
            return new AttendancePolicy(policyId, course, lateThresholdMinutes, latesEqualToAbsent, absentsEqualToDropped, isActive);
        }
    }
}
