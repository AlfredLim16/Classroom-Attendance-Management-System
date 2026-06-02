package assessment;

import application.DatabaseConnection;
import course.Course;
import course.Program;
import course.Semester;
import course.YearLevel;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuizLabScheduleDAO {

    private Connection getConnection() throws SQLException{
        return DatabaseConnection.getConnection();
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            ps.setObject(i + 1, params[i]);
        }
    }

    public Optional<QuizLabSchedule> findById(int id){
        String sql = baseQuery() + " WHERE q.quizId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding quiz/lab schedule", e);
        }
        return Optional.empty();
    }

    public List<QuizLabSchedule> findAll(){
        String sql = baseQuery();
        List<QuizLabSchedule> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                list.add(map(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding all quiz/lab schedules", e);
        }
        return list;
    }

    public QuizLabSchedule save(QuizLabSchedule entity){
        String sql = "INSERT INTO QuizLabSchedule (courseId, quizDate, quizTypeId) VALUES (?, ?, ?)";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            setParams(ps, entity.course().courseId(), Date.valueOf(entity.quizDate()), entity.quizType().getQuizTypeId());
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return new QuizLabSchedule(rs.getInt(1), entity.course(), entity.quizDate(), entity.quizType());
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error saving quiz/lab schedule", e);
        }
        throw new RuntimeException("Failed to retrieve generated key");
    }

    public boolean update(QuizLabSchedule entity){
        String sql = "UPDATE QuizLabSchedule SET courseId = ?, quizDate = ?, quizTypeId = ? WHERE quizId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, entity.course().courseId(), Date.valueOf(entity.quizDate()), entity.quizType().getQuizTypeId(), entity.quizId());
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error updating quiz/lab schedule", e);
        }
    }

    public boolean deleteById(int id){
        String sql = "DELETE FROM QuizLabSchedule WHERE quizId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw new RuntimeException("Error deleting quiz/lab schedule", e);
        }
    }

    public boolean existsById(int id){
        String sql = "SELECT 1 FROM QuizLabSchedule WHERE quizId = ?";
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking quiz/lab schedule existence", e);
        }
    }

    public List<QuizLabSchedule> findByCourseId(int courseId){
        String sql = baseQuery() + " WHERE q.courseId = ?";
        List<QuizLabSchedule> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, courseId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding quiz/lab schedules by course", e);
        }
        return list;
    }

    public List<QuizLabSchedule> findByQuizDate(LocalDate date){
        String sql = baseQuery() + " WHERE q.quizDate = ?";
        List<QuizLabSchedule> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, Date.valueOf(date));
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding quiz/lab schedules by date", e);
        }
        return list;
    }

    public List<QuizLabSchedule> findByQuizType(QuizType type){
        String sql = baseQuery() + " WHERE q.quizTypeId = ?";
        List<QuizLabSchedule> list = new ArrayList<>();
        try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            setParams(ps, type.getQuizTypeId());
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(map(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error finding quiz/lab schedules by type", e);
        }
        return list;
    }

    private String baseQuery(){
        return """
            SELECT
                q.quizId, q.quizDate, q.quizTypeId,
                q.courseId, c.courseCode, c.courseName, c.units, c.yearLevelId AS cYearLevelId,
                c.programId AS cProgramId, cp.programId AS cpProgramId, cp.programName AS cProgramName,
                c.semesterId, csem.semesterId AS csemSemesterId, csem.semesterName, csem.schoolYear, csem.startDate, csem.endDate
            FROM QuizLabSchedule q
            JOIN Course c ON q.courseId = c.courseId
            JOIN Program cp ON c.programId = cp.programId
            JOIN Semester csem ON c.semesterId = csem.semesterId
            """;
    }

    private QuizLabSchedule map(ResultSet rs) throws SQLException{
        Program cProgram = new Program(rs.getInt("cpProgramId"), rs.getString("cProgramName"));
        Semester cSemester = new Semester(
            rs.getInt("csemSemesterId"),
            rs.getString("semesterName"),
            rs.getString("schoolYear"),
            rs.getDate("startDate").toLocalDate(),
            rs.getDate("endDate").toLocalDate()
        );
        Course course = Course.builder()
            .courseId(rs.getInt("courseId"))
            .program(cProgram)
            .courseCode(rs.getString("courseCode"))
            .courseName(rs.getString("courseName"))
            .units(rs.getByte("units"))
            .semester(cSemester)
            .yearLevel(YearLevel.fromId(rs.getInt("cYearLevelId")))
            .build();

        return new QuizLabSchedule(
            rs.getInt("quizId"),
            course,
            rs.getDate("quizDate").toLocalDate(),
            QuizType.fromId(rs.getInt("quizTypeId"))
        );
    }
}
