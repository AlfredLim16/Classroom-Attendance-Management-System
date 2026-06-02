package assessment;

import java.time.LocalDate;
import user.Professor;
import user.Student;

public record MissedQuizFlag(int flagId, Student student, QuizLabSchedule quiz, MissedQuizStatus status, DecisionType decisionType, String remarks, LocalDate decisionDate, Professor decidedBy
    ) {

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private int flagId;
        private Student student;
        private QuizLabSchedule quiz;
        private MissedQuizStatus status;
        private DecisionType decisionType;
        private String remarks;
        private LocalDate decisionDate;
        private Professor decidedBy;

        public Builder flagId(int flagId){
            this.flagId = flagId;
            return this;
        }

        public Builder student(Student student){
            this.student = student;
            return this;
        }

        public Builder quiz(QuizLabSchedule quiz){
            this.quiz = quiz;
            return this;
        }

        public Builder status(MissedQuizStatus status){
            this.status = status;
            return this;
        }

        public Builder decisionType(DecisionType decisionType){
            this.decisionType = decisionType;
            return this;
        }

        public Builder remarks(String remarks){
            this.remarks = remarks;
            return this;
        }

        public Builder decisionDate(LocalDate decisionDate){
            this.decisionDate = decisionDate;
            return this;
        }

        public Builder decidedBy(Professor decidedBy){
            this.decidedBy = decidedBy;
            return this;
        }

        public MissedQuizFlag build(){
            return new MissedQuizFlag(flagId, student, quiz, status, decisionType, remarks, decisionDate, decidedBy);
        }
    }
}
