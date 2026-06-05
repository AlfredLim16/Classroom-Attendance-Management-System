package dao;

import junction.QuizLabSchedule;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface QuizLabScheduleDAO {
    void insert(QuizLabSchedule schedule) throws SQLException, DuplicateEntryException;
    void update(QuizLabSchedule schedule) throws SQLException, NotFoundException;
    void delete(int quizId) throws SQLException, NotFoundException;
    QuizLabSchedule findById(int quizId) throws SQLException, NotFoundException;
    List<QuizLabSchedule> findByCourse(int courseId) throws SQLException;
    List<QuizLabSchedule> findAll() throws SQLException;
}
