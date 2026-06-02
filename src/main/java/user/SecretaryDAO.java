package user;

import application.DatabaseConnection;
import course.Program;
import course.Section;
import course.YearLevel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SecretaryDAO {

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            ps.setObject(i + 1, params[i]);
        }
    }

    public Optional<Secretary> findById(int id){
        String sql = baseQuery() + " WHERE sec.secretaryId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding secretary", e);
        }
        return Optional.empty();
    }

    public Optional<Secretary> findByUserId(int userId){
        String sql = baseQuery() + " WHERE su.userId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, userId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding secretary by user id", e);
        }
        return Optional.empty();
    }

    /** NEW: Lookup by student number (handles sec.2025-0001-BN-0 → 2025-0001-BN-0) */
    public Optional<Secretary> findByStudentNumber(String studentNumber){
        String sql = baseQuery() + " WHERE s.studentNumber = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, studentNumber);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding secretary by student number", e);
        }
        return Optional.empty();
    }

    public List<Student> findStudentsBySectionId(int sectionId){
        String sql = """
            SELECT
                s.studentId, s.studentNumber, s.firstName, s.middleName, s.lastName,
                s.userId, su.userName, su.userPassword, su.roleId,
                s.programId, sp.programName,
                s.yearLevelId,
                s.sectionId, stsec.programId AS stsecProgramId, stsec.yearLevelId AS stsecYearLevelId, stsec.sectionCode,
                stsecPr.programName AS stsecProgramName
            FROM Student s
            JOIN User su ON s.userId = su.userId
            JOIN Program sp ON s.programId = sp.programId
            JOIN Section stsec ON s.sectionId = stsec.sectionId
            JOIN Program stsecPr ON stsec.programId = stsecPr.programId
            WHERE s.sectionId = ?
            """;
        List<Student> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, sectionId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(mapStudent(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding students by section", e);
        }
        return list;
    }

    public List<Secretary> findAll(){
        String sql = baseQuery();
        List<Secretary> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(map(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding all secretaries", e);
        }
        return list;
    }

    public Secretary save(Secretary entity){
        String sql = "INSERT INTO Secretary (studentId, sectionId) VALUES (?, ?)";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(ps, entity.student().studentId(), entity.section().sectionId());
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return new Secretary(rs.getInt(1), entity.student(), entity.section());
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error saving secretary", e);
        }
        throw new RuntimeException("Failed to retrieve generated key");
    }

    public boolean update(Secretary entity){
        String sql = "UPDATE Secretary SET studentId = ?, sectionId = ? WHERE secretaryId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, entity.student().studentId(), entity.section().sectionId(), entity.secretaryId());
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error updating secretary", e);
        }
    }

    public boolean deleteById(int id){
        String sql = "DELETE FROM Secretary WHERE secretaryId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error deleting secretary", e);
        }
    }

    public boolean existsById(int id){
        String sql = "SELECT 1 FROM Secretary WHERE secretaryId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking secretary existence", e);
        }
    }

    private String baseQuery(){
        return """
            SELECT
                sec.secretaryId, sec.studentId, sec.sectionId,
                s.studentNumber, s.firstName AS sFirstName, s.middleName AS sMiddleName, s.lastName AS sLastName,
                s.userId, su.userName, su.userPassword, su.roleId,
                s.programId AS sProgramId, sp.programName AS sProgramName,
                s.yearLevelId AS sYearLevelId,
                s.sectionId AS sSectionId, stsec.programId AS stsecProgramId, stsec.yearLevelId AS stsecYearLevelId, stsec.sectionCode AS stsecSectionCode,
                stsecPr.programName AS stsecProgramName,
                sect.sectionId AS sectSectionId, sect.programId AS sectProgramId, sect.yearLevelId AS sectYearLevelId, sect.sectionCode AS sectSectionCode,
                sectPr.programName AS sectProgramName
            FROM Secretary sec
            JOIN Student s ON sec.studentId = s.studentId
            JOIN User su ON s.userId = su.userId
            JOIN Program sp ON s.programId = sp.programId
            JOIN Section stsec ON s.sectionId = stsec.sectionId
            JOIN Program stsecPr ON stsec.programId = stsecPr.programId
            JOIN Section sect ON sec.sectionId = sect.sectionId
            JOIN Program sectPr ON sect.programId = sectPr.programId
            """;
    }

    private Secretary map(ResultSet rs) throws SQLException{
        User user = new User(
            rs.getInt("userId"),
            rs.getString("userName"),
            rs.getString("userPassword"),
            Role.fromId(rs.getInt("roleId"))
        );
        Program sProgram = new Program(rs.getInt("sProgramId"), rs.getString("sProgramName"));
        Program stsecProgram = new Program(rs.getInt("stsecProgramId"), rs.getString("stsecProgramName"));
        Section stSection = new Section(
            rs.getInt("sSectionId"),
            stsecProgram,
            YearLevel.fromId(rs.getInt("stsecYearLevelId")),
            rs.getString("stsecSectionCode")
        );
        Student student = Student.builder()
            .studentId(rs.getInt("studentId"))
            .user(user)
            .studentNumber(rs.getString("studentNumber"))
            .firstName(rs.getString("sFirstName"))
            .middleName(rs.getString("sMiddleName"))
            .lastName(rs.getString("sLastName"))
            .program(sProgram)
            .yearLevel(YearLevel.fromId(rs.getInt("sYearLevelId")))
            .section(stSection)
            .build();

        Program sectProgram = new Program(rs.getInt("sectProgramId"), rs.getString("sectProgramName"));
        Section section = new Section(
            rs.getInt("sectSectionId"),
            sectProgram,
            YearLevel.fromId(rs.getInt("sectYearLevelId")),
            rs.getString("sectSectionCode")
        );
        return new Secretary(rs.getInt("secretaryId"), student, section);
    }

    private Student mapStudent(ResultSet rs) throws SQLException{
        User user = new User(
            rs.getInt("userId"),
            rs.getString("userName"),
            rs.getString("userPassword"),
            Role.fromId(rs.getInt("roleId"))
        );
        Program program = new Program(rs.getInt("programId"), rs.getString("programName"));
        Program stsecProgram = new Program(rs.getInt("stsecProgramId"), rs.getString("stsecProgramName"));
        Section section = new Section(
            rs.getInt("sectionId"),
            stsecProgram,
            YearLevel.fromId(rs.getInt("stsecYearLevelId")),
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