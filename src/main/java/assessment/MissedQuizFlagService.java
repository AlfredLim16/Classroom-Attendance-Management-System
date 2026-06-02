package assessment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import user.Professor;
import user.Student;

public class MissedQuizFlagService {

    private final MissedQuizFlagDAO missedQuizFlagDAO = new MissedQuizFlagDAO();

    public MissedQuizFlag createMissedQuizFlag(Student student, QuizLabSchedule quiz, MissedQuizStatus status, DecisionType decisionType, String remarks, LocalDate decisionDate, Professor decidedBy){
        MissedQuizFlag flag = MissedQuizFlag.builder()
            .flagId(0)
            .student(student)
            .quiz(quiz)
            .status(status)
            .decisionType(decisionType)
            .remarks(remarks)
            .decisionDate(decisionDate)
            .decidedBy(decidedBy)
            .build();
        return missedQuizFlagDAO.save(flag);
    }

    public Optional<MissedQuizFlag> getMissedQuizFlagById(int id){
        return missedQuizFlagDAO.findById(id);
    }

    public List<MissedQuizFlag> getAllMissedQuizFlags(){
        return missedQuizFlagDAO.findAll();
    }

    public List<MissedQuizFlag> getMissedQuizFlagsByStudent(int studentId){
        return missedQuizFlagDAO.findByStudentId(studentId);
    }

    public List<MissedQuizFlag> getMissedQuizFlagsByQuiz(int quizId){
        return missedQuizFlagDAO.findByQuizId(quizId);
    }

    public List<MissedQuizFlag> getMissedQuizFlagsByStatus(MissedQuizStatus status){
        return missedQuizFlagDAO.findByStatus(status);
    }

    public List<MissedQuizFlag> getMissedQuizFlagsByDecisionType(DecisionType decisionType){
        return missedQuizFlagDAO.findByDecisionType(decisionType);
    }

    public boolean updateMissedQuizFlag(MissedQuizFlag flag){
        return missedQuizFlagDAO.update(flag);
    }

    public boolean deleteMissedQuizFlag(int id){
        return missedQuizFlagDAO.deleteById(id);
    }
}
