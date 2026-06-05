package junction;

import core.Professor;
import core.Student;
import lookup.DecisionType;
import lookup.MissedQuizStatus;
import validations.QuizLabScheduleValidator;
import java.time.LocalDate;
import java.util.Objects;

public record MissedQuizFlag(
    int flagId, Student student, QuizLabSchedule quiz, MissedQuizStatus missedQuizStatus, DecisionType decisionType, String remarks, LocalDate decisionDate, Professor decidedByProfessor) {

    public MissedQuizFlag {
        QuizLabScheduleValidator.validateMissedQuizFlag(student, quiz, missedQuizStatus, decisionType, decidedByProfessor);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        MissedQuizFlag that = (MissedQuizFlag) object;
        return flagId == that.flagId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(flagId);
    }

    @Override
    public String toString() {
        return "MissedQuizFlag{id=" + flagId + ", student=" + (student != null ? student.studentNumber() : "null") + ", status=" + missedQuizStatus + ", decision=" + decisionType + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int flagId;
        private Student student;
        private QuizLabSchedule quiz;
        private MissedQuizStatus missedQuizStatus;
        private DecisionType decisionType;
        private String remarks;
        private LocalDate decisionDate;
        private Professor decidedByProfessor;

        public Builder flagId(int flagId) {
            this.flagId = flagId;
            return this;
        }

        public Builder student(Student student) {
            this.student = student;
            return this;
        }

        public Builder quiz(QuizLabSchedule quiz) {
            this.quiz = quiz;
            return this;
        }

        public Builder missedQuizStatus(MissedQuizStatus missedQuizStatus) {
            this.missedQuizStatus = missedQuizStatus;
            return this;
        }

        public Builder decisionType(DecisionType decisionType) {
            this.decisionType = decisionType;
            return this;
        }

        public Builder remarks(String remarks) {
            this.remarks = remarks;
            return this;
        }

        public Builder decisionDate(LocalDate decisionDate) {
            this.decisionDate = decisionDate;
            return this;
        }

        public Builder decidedByProfessor(Professor decidedByProfessor) {
            this.decidedByProfessor = decidedByProfessor;
            return this;
        }

        public MissedQuizFlag build() {
            return new MissedQuizFlag(flagId, student, quiz, missedQuizStatus, decisionType, remarks, decisionDate, decidedByProfessor);
        }
    }
}
