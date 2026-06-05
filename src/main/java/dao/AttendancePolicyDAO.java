package dao;

import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import java.sql.SQLException;
import java.util.List;
import junction.AttendancePolicy;

public interface AttendancePolicyDAO {
    void insert(AttendancePolicy policy) throws SQLException, DuplicateEntryException;
    void update(AttendancePolicy policy) throws SQLException, NotFoundException;
    void delete(int policyId) throws SQLException, NotFoundException;
    
    AttendancePolicy findById(int policyId) throws SQLException, NotFoundException;
    AttendancePolicy findActiveByCourse(int courseId) throws SQLException, NotFoundException;
    List<AttendancePolicy> findByCourse(int courseId) throws SQLException;
    List<AttendancePolicy> findAll() throws SQLException;
}
