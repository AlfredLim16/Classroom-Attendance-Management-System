package dao;

import junction.MissedQuizFlag;
import lookup.MissedQuizStatus;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface MissedQuizFlagDAO {
    void insert(MissedQuizFlag flag) throws SQLException, DuplicateEntryException;
    void update(MissedQuizFlag flag) throws SQLException, NotFoundException;
    void delete(int flagId) throws SQLException, NotFoundException;
    MissedQuizFlag findById(int flagId) throws SQLException, NotFoundException;
    List<MissedQuizFlag> findByStudent(int studentId) throws SQLException;
    List<MissedQuizFlag> findByQuiz(int quizId) throws SQLException;
    List<MissedQuizFlag> findByStatus(MissedQuizStatus status) throws SQLException;
    List<MissedQuizFlag> findAll() throws SQLException;
}
