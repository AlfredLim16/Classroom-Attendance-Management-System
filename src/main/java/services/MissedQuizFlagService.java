package services;

import core.Professor;
import dao.*;
import exceptions.NotFoundException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import junction.MissedQuizFlag;
import lookup.DecisionType;

public class MissedQuizFlagService {

    private final MissedQuizFlagDAO flagDAO;
    private final MissedQuizService missedQuizService;

    public MissedQuizFlagService(){
        ProgramDAO programDAO = new ProgramDAOImpl();
        SemesterDAO semesterDAO = new SemesterDAOImpl();
        SectionDAO sectionDAO = new SectionDAOImpl(programDAO);
        CourseDAO courseDAO = new CourseDAOImpl(programDAO, semesterDAO);
        UserDAO userDAO = new UserDAOImpl();
        StudentDAO studentDAO = new StudentDAOImpl(userDAO, programDAO, sectionDAO);
        ProfessorDAO professorDAO = new ProfessorDAOImpl(userDAO);
        QuizLabScheduleDAO quizDAO = new QuizLabScheduleDAOImpl(courseDAO);
        this.flagDAO = new MissedQuizFlagDAOImpl(studentDAO, quizDAO, professorDAO);
        this.missedQuizService = new MissedQuizService(flagDAO);
    }

    public List<MissedQuizFlag> getAllMissedQuizFlags(){
        try{
            return flagDAO.findAll();
        }catch(SQLException e){
            System.err.println("[MissedQuizFlagService] getAllMissedQuizFlags: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean processMissedQuizFlag(int flagId, DecisionType decision, String remarks, Professor professor){
        try{
            missedQuizService.resolveFlag(flagId, decision, remarks, professor);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[MissedQuizFlagService] processMissedQuizFlag: " + e.getMessage());
            return false;
        }
    }
}
