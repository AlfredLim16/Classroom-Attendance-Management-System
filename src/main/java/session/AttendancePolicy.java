package session;

import course.Course;

public record AttendancePolicy(
    int policyId,
    Course course,
    int lateThresholdMinutes,
    int latesEqualToAbsent,
    int absentsEqualToDropped,
    boolean active
    ) {

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private int policyId;
        private Course course;
        private int lateThresholdMinutes;
        private int latesEqualToAbsent;
        private int absentsEqualToDropped;
        private boolean active = true;

        public Builder policyId(int policyId){
            this.policyId = policyId;
            return this;
        }

        public Builder course(Course course){
            this.course = course;
            return this;
        }

        public Builder lateThresholdMinutes(int lateThresholdMinutes){
            this.lateThresholdMinutes = lateThresholdMinutes;
            return this;
        }

        public Builder latesEqualToAbsent(int latesEqualToAbsent){
            this.latesEqualToAbsent = latesEqualToAbsent;
            return this;
        }

        public Builder absentsEqualToDropped(int absentsEqualToDropped){
            this.absentsEqualToDropped = absentsEqualToDropped;
            return this;
        }

        public Builder active(boolean active){
            this.active = active;
            return this;
        }

        public AttendancePolicy build(){
            return new AttendancePolicy(policyId, course, lateThresholdMinutes, latesEqualToAbsent, absentsEqualToDropped, active);
        }
    }
}
