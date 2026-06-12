package services;

import core.Course;
import dao.*;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import junction.QuizLabSchedule;
import lookup.QuizType;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class QuizLabScheduleService {

    private final QuizLabScheduleDAO quizDAO;

    public QuizLabScheduleService(){
        ProgramDAO programDAO   = new ProgramDAOImpl();
        SemesterDAO semesterDAO = new SemesterDAOImpl();
        CourseDAO courseDAO     = new CourseDAOImpl(programDAO, semesterDAO);
        this.quizDAO            = new QuizLabScheduleDAOImpl(courseDAO);
    }

    public List<QuizLabSchedule> getSchedulesByCourse(int courseId){
        try{
            return quizDAO.findByCourse(courseId);
        }catch(SQLException e){
            System.err.println("[QuizLabScheduleService] getSchedulesByCourse: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean createSchedule(Course course, LocalDate date, QuizType type){
        try{
            QuizLabSchedule schedule = QuizLabSchedule.builder()
                .course(course).quizDate(date).quizType(type).build();
            quizDAO.insert(schedule);
            return true;
        }catch(SQLException | DuplicateEntryException e){
            System.err.println("[QuizLabScheduleService] createSchedule: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSchedule(int quizId){
        try{
            quizDAO.delete(quizId);
            return true;
        }catch(SQLException | NotFoundException e){
            System.err.println("[QuizLabScheduleService] deleteSchedule: " + e.getMessage());
            return false;
        }
    }
}
