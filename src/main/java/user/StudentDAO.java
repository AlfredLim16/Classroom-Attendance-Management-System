package user;

import application.DatabaseConnection;
import course.Program;
import course.Section;
import course.YearLevel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAO {

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            ps.setObject(i + 1, params[i]);
        }
    }

    public Optional<Student> findById(int id){
        String sql = """
            SELECT s.studentId, s.studentNumber, s.firstName, s.middleName, s.lastName,
                   s.programId, pr.programId AS prProgramId, pr.programName,
                   s.yearLevelId,
                   s.sectionId, sec.programId AS secProgramId, sec.yearLevelId AS secYearLevelId, sec.sectionCode,
                   secPr.programName AS secProgramName,
                   s.userId, u.userName, u.userPassword, u.roleId
            FROM Student s
            JOIN User u ON s.userId = u.userId
            JOIN Program pr ON s.programId = pr.programId
            JOIN Section sec ON s.sectionId = sec.sectionId
            JOIN Program secPr ON sec.programId = secPr.programId
            WHERE s.studentId = ?
            """;
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding student", e);
        }
        return Optional.empty();
    }

    public List<Student> findAll(){
        String sql = """
            SELECT s.studentId, s.studentNumber, s.firstName, s.middleName, s.lastName,
                   s.programId, pr.programId AS prProgramId, pr.programName,
                   s.yearLevelId,
                   s.sectionId, sec.programId AS secProgramId, sec.yearLevelId AS secYearLevelId, sec.sectionCode,
                   secPr.programName AS secProgramName,
                   s.userId, u.userName, u.userPassword, u.roleId
            FROM Student s
            JOIN User u ON s.userId = u.userId
            JOIN Program pr ON s.programId = pr.programId
            JOIN Section sec ON s.sectionId = sec.sectionId
            JOIN Program secPr ON sec.programId = secPr.programId
            """;
        List<Student> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(map(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding all students", e);
        }
        return list;
    }

    public Student save(Student entity){
        String sql = """
            INSERT INTO Student (userId, studentNumber, firstName, middleName, lastName, programId, yearLevelId, sectionId)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(ps, entity.user().userId(), entity.studentNumber(), entity.firstName(),
                entity.middleName(), entity.lastName(), entity.program().programId(),
                entity.yearLevel().getYearLevelId(), entity.section().sectionId());
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return Student.builder()
                        .studentId(rs.getInt(1))
                        .user(entity.user())
                        .studentNumber(entity.studentNumber())
                        .firstName(entity.firstName())
                        .middleName(entity.middleName())
                        .lastName(entity.lastName())
                        .program(entity.program())
                        .yearLevel(entity.yearLevel())
                        .section(entity.section())
                        .build();
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error saving student", e);
        }
        throw new RuntimeException("Failed to retrieve generated key");
    }

    public boolean update(Student entity){
        String sql = """
            UPDATE Student
            SET userId = ?, studentNumber = ?, firstName = ?, middleName = ?, lastName = ?,
                programId = ?, yearLevelId = ?, sectionId = ?
            WHERE studentId = ?
            """;
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, entity.user().userId(), entity.studentNumber(), entity.firstName(),
                entity.middleName(), entity.lastName(), entity.program().programId(),
                entity.yearLevel().getYearLevelId(), entity.section().sectionId(),
                entity.studentId());
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error updating student", e);
        }
    }

    public boolean deleteById(int id){
        String sql = "DELETE FROM Student WHERE studentId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error deleting student", e);
        }
    }

    public boolean existsById(int id){
        String sql = "SELECT 1 FROM Student WHERE studentId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking student existence", e);
        }
    }

    public Optional<Student> findByStudentNumber(String studentNumber){
        String sql = """
            SELECT s.studentId, s.studentNumber, s.firstName, s.middleName, s.lastName,
                   s.programId, pr.programId AS prProgramId, pr.programName,
                   s.yearLevelId,
                   s.sectionId, sec.programId AS secProgramId, sec.yearLevelId AS secYearLevelId, sec.sectionCode,
                   secPr.programName AS secProgramName,
                   s.userId, u.userName, u.userPassword, u.roleId
            FROM Student s
            JOIN User u ON s.userId = u.userId
            JOIN Program pr ON s.programId = pr.programId
            JOIN Section sec ON s.sectionId = sec.sectionId
            JOIN Program secPr ON sec.programId = secPr.programId
            WHERE s.studentNumber = ?
            """;
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, studentNumber);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding student by number", e);
        }
        return Optional.empty();
    }

    public List<Student> findBySectionId(int sectionId){
        String sql = """
            SELECT s.studentId, s.studentNumber, s.firstName, s.middleName, s.lastName,
                   s.programId, pr.programId AS prProgramId, pr.programName,
                   s.yearLevelId,
                   s.sectionId, sec.programId AS secProgramId, sec.yearLevelId AS secYearLevelId, sec.sectionCode,
                   secPr.programName AS secProgramName,
                   s.userId, u.userName, u.userPassword, u.roleId
            FROM Student s
            JOIN User u ON s.userId = u.userId
            JOIN Program pr ON s.programId = pr.programId
            JOIN Section sec ON s.sectionId = sec.sectionId
            JOIN Program secPr ON sec.programId = secPr.programId
            WHERE s.sectionId = ?
            """;
        List<Student> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, sectionId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding students by section", e);
        }
        return list;
    }

    private Student map(ResultSet rs) throws SQLException{
        User user = new User(
            rs.getInt("userId"),
            rs.getString("userName"),
            rs.getString("userPassword"),
            Role.fromId(rs.getInt("roleId"))
        );
        Program program = new Program(rs.getInt("prProgramId"), rs.getString("programName"));
        Program secProgram = new Program(rs.getInt("secProgramId"), rs.getString("secProgramName"));
        Section section = new Section(
            rs.getInt("sectionId"),
            secProgram,
            YearLevel.fromId(rs.getInt("secYearLevelId")),
            rs.getString("sectionCode")
        );
        return Student.builder()
            .studentId(rs.getInt("studentId"))
            .user(user)
            .studentNumber(rs.getString("studentNumber"))
            .firstName(rs.getString("firstName"))
            .middleName(rs.getString("middleName"))
            .lastName(rs.getString("lastName"))
            .program(program)
            .yearLevel(YearLevel.fromId(rs.getInt("yearLevelId")))
            .section(section)
            .build();
    }
}
