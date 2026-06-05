package services;

import core.Course;
import dao.*;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import java.sql.SQLException;
import java.util.Optional;
import junction.AttendancePolicy;

public class AttendancePolicyService {

    private final AttendancePolicyDAO policyDAO;

    public AttendancePolicyService(){
        ProgramDAO programDAO = new ProgramDAOImpl();
        SemesterDAO semesterDAO = new SemesterDAOImpl();
        CourseDAO courseDAO = new CourseDAOImpl(programDAO, semesterDAO);
        this.policyDAO = new AttendancePolicyDAOImpl(courseDAO);
    }

    public Optional<AttendancePolicy> getAttendancePolicyByCourse(int courseId){
        try{
            return Optional.of(policyDAO.findActiveByCourse(courseId));
        }catch(SQLException | NotFoundException e){
            return Optional.empty();
        }
    }

    public boolean updateAttendancePolicy(AttendancePolicy policy){
        try{
            policyDAO.update(policy);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[AttendancePolicyService] update: " + e.getMessage());
            return false;
        }
    }

    public AttendancePolicy createAttendancePolicy(Course course, int lateThreshold, int latesEqualToAbsent, int absentsEqualToDropped, boolean isActive){
        try{
            AttendancePolicy policy = AttendancePolicy.builder().course(course).lateThresholdMinutes(lateThreshold).latesEqualToAbsent(latesEqualToAbsent).absentsEqualToDropped(absentsEqualToDropped).isActive(isActive).build();
            policyDAO.insert(policy);
            return policy;
        }catch(SQLException | DuplicateEntryException e){
            System.err.println("[AttendancePolicyService] create: " + e.getMessage());
            return null;
        }
    }
}
