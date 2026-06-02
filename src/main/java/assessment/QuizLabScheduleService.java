package assessment;

import course.Course;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class QuizLabScheduleService {

    private final QuizLabScheduleDAO quizLabScheduleDAO = new QuizLabScheduleDAO();

    public QuizLabSchedule createQuizLabSchedule(Course course, LocalDate quizDate, QuizType quizType){
        QuizLabSchedule quiz = new QuizLabSchedule(0, course, quizDate, quizType);
        return quizLabScheduleDAO.save(quiz);
    }

    public Optional<QuizLabSchedule> getQuizLabScheduleById(int id){
        return quizLabScheduleDAO.findById(id);
    }

    public List<QuizLabSchedule> getAllQuizLabSchedules(){
        return quizLabScheduleDAO.findAll();
    }

    public List<QuizLabSchedule> getQuizLabSchedulesByCourse(int courseId){
        return quizLabScheduleDAO.findByCourseId(courseId);
    }

    public List<QuizLabSchedule> getQuizLabSchedulesByDate(LocalDate date){
        return quizLabScheduleDAO.findByQuizDate(date);
    }

    public List<QuizLabSchedule> getQuizLabSchedulesByType(QuizType type){
        return quizLabScheduleDAO.findByQuizType(type);
    }

    public boolean updateQuizLabSchedule(QuizLabSchedule quiz){
        return quizLabScheduleDAO.update(quiz);
    }

    public boolean deleteQuizLabSchedule(int id){
        return quizLabScheduleDAO.deleteById(id);
    }
}
