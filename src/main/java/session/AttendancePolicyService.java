package session;

import course.Course;
import java.util.List;
import java.util.Optional;

public class AttendancePolicyService {

    private final AttendancePolicyDAO attendancePolicyDAO = new AttendancePolicyDAO();

    public AttendancePolicy createAttendancePolicy(Course course, int lateThresholdMinutes, int latesEqualToAbsent, int absentsEqualToDropped, boolean isActive){
        AttendancePolicy policy = AttendancePolicy.builder()
            .policyId(0)
            .course(course)
            .lateThresholdMinutes(lateThresholdMinutes)
            .latesEqualToAbsent(latesEqualToAbsent)
            .absentsEqualToDropped(absentsEqualToDropped)
            .active(isActive)
            .build();
        return attendancePolicyDAO.save(policy);
    }

    public Optional<AttendancePolicy> getAttendancePolicyById(int id){
        return attendancePolicyDAO.findById(id);
    }

    public List<AttendancePolicy> getAllAttendancePolicies(){
        return attendancePolicyDAO.findAll();
    }

    public Optional<AttendancePolicy> getAttendancePolicyByCourse(int courseId){
        return attendancePolicyDAO.findByCourseId(courseId);
    }

    public List<AttendancePolicy> getActivePolicies(boolean active){
        return attendancePolicyDAO.findByActive(active);
    }

    public boolean updateAttendancePolicy(AttendancePolicy policy){
        return attendancePolicyDAO.update(policy);
    }

    public boolean deleteAttendancePolicy(int id){
        return attendancePolicyDAO.deleteById(id);
    }
}
