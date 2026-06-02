package assessment;

import excuse.ExcuseLetter;
import excuse.ExcuseLetterDAO;
import excuse.ExcuseStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import session.AttendanceDAO;
import user.Professor;
import user.Student;

public class MissedQuizProcessingService {

    private final MissedQuizFlagDAO missedQuizFlagDAO = new MissedQuizFlagDAO();
    private final ExcuseLetterDAO excuseLetterDAO = new ExcuseLetterDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    public MissedQuizFlag flagMissedQuiz(Student student, QuizLabSchedule quiz, String remarks){
        List<ExcuseLetter> excuses = excuseLetterDAO.findByStudentId(student.studentId());
        boolean hasValidExcuse = false;
        for(ExcuseLetter excuse : excuses){
            if(excuse.status() == ExcuseStatus.APPROVED
                && excuse.absentDate().equals(quiz.quizDate())
                && excuse.course().courseId() == quiz.course().courseId()){
                hasValidExcuse = true;
                break;
            }
        }

        MissedQuizStatus initialStatus = hasValidExcuse ? MissedQuizStatus.EXCUSED : MissedQuizStatus.PENDING;
        DecisionType initialDecision = hasValidExcuse ? DecisionType.EXCUSED_ABSENCE : null;

        MissedQuizFlag flag = MissedQuizFlag.builder()
            .flagId(0)
            .student(student)
            .quiz(quiz)
            .status(initialStatus)
            .decisionType(initialDecision)
            .remarks(remarks)
            .decisionDate(hasValidExcuse ? LocalDate.now() : null)
            .decidedBy(null)
            .build();

        return missedQuizFlagDAO.save(flag);
    }

    public boolean processMissedQuizFlag(int flagId, DecisionType decisionType, String remarks, Professor decidedBy){
        Optional<MissedQuizFlag> opt = missedQuizFlagDAO.findById(flagId);
        if(opt.isEmpty()){
            return false;
        }

        MissedQuizFlag old = opt.get();
        MissedQuizStatus newStatus = determineStatusFromDecision(decisionType);

        MissedQuizFlag updated = MissedQuizFlag.builder()
            .flagId(old.flagId())
            .student(old.student())
            .quiz(old.quiz())
            .status(newStatus)
            .decisionType(decisionType)
            .remarks(remarks != null ? remarks : old.remarks())
            .decisionDate(LocalDate.now())
            .decidedBy(decidedBy)
            .build();

        return missedQuizFlagDAO.update(updated);
    }

    private MissedQuizStatus determineStatusFromDecision(DecisionType decisionType){
        return switch(decisionType){
            case MAKEUP, EXEMPTED, EXCUSED_ABSENCE ->
                MissedQuizStatus.APPROVED;
            case FAILED ->
                MissedQuizStatus.REJECTED;
        };
    }

    public List<MissedQuizFlag> getPendingFlagsForProfessor(int professorId){
        List<MissedQuizFlag> all = missedQuizFlagDAO.findAll();
        return all.stream()
            .filter(f -> f.status() == MissedQuizStatus.PENDING)
            .filter(f -> f.quiz().course().courseId() == professorId)
            .toList();
    }
}
