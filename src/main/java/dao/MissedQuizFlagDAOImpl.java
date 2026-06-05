package dao;

import core.Professor;
import core.Student;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import junction.MissedQuizFlag;
import junction.QuizLabSchedule;
import lookup.DecisionType;
import lookup.MissedQuizStatus;

public class MissedQuizFlagDAOImpl implements MissedQuizFlagDAO {

    private final StudentDAO studentDAO;
    private final QuizLabScheduleDAO quizDAO;
    private final ProfessorDAO professorDAO;

    public MissedQuizFlagDAOImpl(StudentDAO studentDAO, QuizLabScheduleDAO quizDAO, ProfessorDAO professorDAO) {
        this.studentDAO = studentDAO;
        this.quizDAO = quizDAO;
        this.professorDAO = professorDAO;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private MissedQuizFlag mapRow(ResultSet rs) throws SQLException {
        try {
            Student student = studentDAO.findById(rs.getInt("studentId"));
            QuizLabSchedule quiz = quizDAO.findById(rs.getInt("quizId"));
            MissedQuizStatus status = MissedQuizStatus.fromId(rs.getInt("missedQuizStatusId"));
            
            DecisionType decisionType = null;
            int decisionTypeId = rs.getInt("decisionTypeId");
            if (!rs.wasNull()) {
                decisionType = DecisionType.fromId(decisionTypeId);
            }

            Professor professor = null;
            int professorId = rs.getInt("decidedByProfessorId");
            if (!rs.wasNull()) {
                professor = professorDAO.findById(professorId);
            }

            Date decisionDateSql = rs.getDate("decisionDate");

            return MissedQuizFlag.builder()
                    .flagId(rs.getInt("flagId"))
                    .student(student)
                    .quiz(quiz)
                    .missedQuizStatus(status)
                    .decisionType(decisionType)
                    .remarks(rs.getString("remarks"))
                    .decisionDate(decisionDateSql != null ? decisionDateSql.toLocalDate() : null)
                    .decidedByProfessor(professor)
                    .build();
        } catch (ValidationException e) {
            throw new SQLException("Invalid enum id in database for flagId=" + rs.getInt("flagId") + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void insert(MissedQuizFlag flag) throws SQLException, DuplicateEntryException {
        String sql = """
                INSERT INTO MissedQuizFlag
                (studentId, quizId, missedQuizStatusId, decisionTypeId, remarks, decisionDate, decidedByProfessorId)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, flag.student().studentId());
            ps.setInt(2, flag.quiz().quizId());
            ps.setInt(3, flag.missedQuizStatus().getMissedQuizStatusId());
            if (flag.decisionType() != null) {
                ps.setInt(4, flag.decisionType().getDecisionTypeId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, flag.remarks());
            if (flag.decisionDate() != null) {
                ps.setDate(6, Date.valueOf(flag.decisionDate()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            if (flag.decidedByProfessor() != null) {
                ps.setInt(7, flag.decidedByProfessor().professorId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateEntryException("MissedQuizFlag",
                    "studentId+quizId",
                    flag.student().studentId() + "+" + flag.quiz().quizId());
        }
    }

    @Override
    public void update(MissedQuizFlag flag) throws SQLException, NotFoundException {
        String sql = """
                UPDATE MissedQuizFlag
                SET studentId = ?, quizId = ?, missedQuizStatusId = ?, decisionTypeId = ?,
                    remarks = ?, decisionDate = ?, decidedByProfessorId = ?
                WHERE flagId = ?
                """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, flag.student().studentId());
            ps.setInt(2, flag.quiz().quizId());
            ps.setInt(3, flag.missedQuizStatus().getMissedQuizStatusId());
            if (flag.decisionType() != null) {
                ps.setInt(4, flag.decisionType().getDecisionTypeId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, flag.remarks());
            if (flag.decisionDate() != null) {
                ps.setDate(6, Date.valueOf(flag.decisionDate()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            if (flag.decidedByProfessor() != null) {
                ps.setInt(7, flag.decidedByProfessor().professorId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setInt(8, flag.flagId());
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("MissedQuizFlag", flag.flagId());
            }
        }
    }

    @Override
    public void delete(int flagId) throws SQLException, NotFoundException {
        String sql = "DELETE FROM MissedQuizFlag WHERE flagId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, flagId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("MissedQuizFlag", flagId);
            }
        }
    }

    @Override
    public MissedQuizFlag findById(int flagId) throws SQLException, NotFoundException {
        String sql = "SELECT flagId, studentId, quizId, missedQuizStatusId, decisionTypeId, remarks, decisionDate, decidedByProfessorId FROM MissedQuizFlag WHERE flagId = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, flagId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                throw new NotFoundException("MissedQuizFlag", flagId);
            }
        }
    }

    @Override
    public List<MissedQuizFlag> findByStudent(int studentId) throws SQLException {
        String sql = "SELECT flagId, studentId, quizId, missedQuizStatusId, decisionTypeId, remarks, decisionDate, decidedByProfessorId FROM MissedQuizFlag WHERE studentId = ?";
        return queryList(sql, studentId);
    }

    @Override
    public List<MissedQuizFlag> findByQuiz(int quizId) throws SQLException {
        String sql = "SELECT flagId, studentId, quizId, missedQuizStatusId, decisionTypeId, remarks, decisionDate, decidedByProfessorId FROM MissedQuizFlag WHERE quizId = ?";
        return queryList(sql, quizId);
    }

    @Override
    public List<MissedQuizFlag> findByStatus(MissedQuizStatus status) throws SQLException {
        String sql = "SELECT flagId, studentId, quizId, missedQuizStatusId, decisionTypeId, remarks, decisionDate, decidedByProfessorId FROM MissedQuizFlag WHERE missedQuizStatusId = ?";
        return queryList(sql, status.getMissedQuizStatusId());
    }

    @Override
    public List<MissedQuizFlag> findAll() throws SQLException {
        String sql = "SELECT flagId, studentId, quizId, missedQuizStatusId, decisionTypeId, remarks, decisionDate, decidedByProfessorId FROM MissedQuizFlag";
        List<MissedQuizFlag> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private List<MissedQuizFlag> queryList(String sql, int id) throws SQLException {
        List<MissedQuizFlag> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }
}
