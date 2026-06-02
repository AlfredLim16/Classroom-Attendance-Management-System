package assessment;

import course.Course;
import java.time.LocalDate;
import java.util.Objects;

public record QuizLabSchedule(int quizId, Course course, LocalDate quizDate, QuizType quizType) {

    public QuizLabSchedule{
        if(course == null){
            throw new IllegalArgumentException("course is required");
        }
        if(quizDate == null){
            throw new IllegalArgumentException("quizDate is required");
        }
        if(quizType == null){
            throw new IllegalArgumentException("quizType is required");
        }
    }

    @Override
    public boolean equals(Object object){
        if(this == object){
            return true;
        }
        if(object == null || getClass() != object.getClass()){
            return false;
        }
        QuizLabSchedule that = (QuizLabSchedule) object;
        return quizId == that.quizId;
    }

    @Override
    public int hashCode(){
        return Objects.hash(quizId);
    }

    @Override
    public String toString(){
        return "QuizLabSchedule{id=" + quizId + ", date=" + quizDate + ", type=" + quizType + "}";
    }
}
