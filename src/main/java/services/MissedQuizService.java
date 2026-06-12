package services;

import core.Professor;
import core.Student;
import dao.*;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import junction.MissedQuizFlag;
import junction.QuizLabSchedule;
import lookup.DecisionType;
import lookup.MissedQuizStatus;

public class MissedQuizService {

    private final MissedQuizFlagDAO flagDAO;

    public MissedQuizService(){
        ProgramDAO programDAO = new ProgramDAOImpl();
        SemesterDAO semesterDAO = new SemesterDAOImpl();
        SectionDAO sectionDAO = new SectionDAOImpl(programDAO);
        CourseDAO courseDAO = new CourseDAOImpl(programDAO, semesterDAO);
        UserDAO userDAO = new UserDAOImpl();
        StudentDAO studentDAO = new StudentDAOImpl(userDAO, programDAO, sectionDAO);
        ProfessorDAO professorDAO = new ProfessorDAOImpl(userDAO);
        QuizLabScheduleDAO quizDAO = new QuizLabScheduleDAOImpl(courseDAO);
        this.flagDAO = new MissedQuizFlagDAOImpl(studentDAO, quizDAO, professorDAO);
    }

    public MissedQuizService(MissedQuizFlagDAO flagDAO){
        this.flagDAO = flagDAO;
    }

    public void flagMissedQuiz(Student student, QuizLabSchedule quiz)
        throws SQLException, DuplicateEntryException{

        MissedQuizFlag flag = MissedQuizFlag.builder()
            .student(student)
            .quiz(quiz)
            .missedQuizStatus(MissedQuizStatus.PENDING)
            .decisionType(null)
            .remarks(null)
            .decisionDate(null)
            .decidedByProfessor(null)
            .build();

        flagDAO.insert(flag);
    }

    public void resolveFlag(int flagId, DecisionType decision,
        String remarks, Professor professor)
        throws SQLException, NotFoundException{

        MissedQuizFlag existing = flagDAO.findById(flagId);

        MissedQuizStatus newStatus = switch(decision){
            case MAKEUP ->
                MissedQuizStatus.APPROVED;
            case ZERO ->
                MissedQuizStatus.REJECTED;
            case EXCUSED_ABSENCE ->
                MissedQuizStatus.EXCUSED;
        };

        MissedQuizFlag resolved = MissedQuizFlag.builder()
            .flagId(existing.flagId())
            .student(existing.student())
            .quiz(existing.quiz())
            .missedQuizStatus(newStatus)
            .decisionType(decision)
            .remarks(remarks)
            .decisionDate(LocalDate.now())
            .decidedByProfessor(professor)
            .build();

        flagDAO.update(resolved);
    }

    public List<MissedQuizFlag> getPendingFlags() throws SQLException{
        return flagDAO.findByStatus(MissedQuizStatus.PENDING);
    }

    public List<MissedQuizFlag> getFlagsByStudent(int studentId) throws SQLException{
        return flagDAO.findByStudent(studentId);
    }

    public List<MissedQuizFlag> getFlagsByQuiz(int quizId) throws SQLException{
        return flagDAO.findByQuiz(quizId);
    }

    public List<MissedQuizFlag> getAll() throws SQLException{
        return flagDAO.findAll();
    }
}
