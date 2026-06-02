package excuse;

import course.Course;
import java.time.LocalDate;
import java.time.LocalDateTime;
import user.Student;
import user.User;

public record ExcuseLetter(
    int excuseId,
    Student student,
    Course course,
    LocalDate absentDate,
    String reason,
    String supportingDocumentPath,
    ExcuseStatus status,
    User reviewedBy,
    LocalDateTime submittedDate,
    LocalDateTime reviewedDate
    ) {

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private int excuseId;
        private Student student;
        private Course course;
        private LocalDate absentDate;
        private String reason;
        private String supportingDocumentPath;
        private ExcuseStatus status;
        private User reviewedBy;
        private LocalDateTime submittedDate;
        private LocalDateTime reviewedDate;

        public Builder excuseId(int excuseId){
            this.excuseId = excuseId;
            return this;
        }

        public Builder student(Student student){
            this.student = student;
            return this;
        }

        public Builder course(Course course){
            this.course = course;
            return this;
        }

        public Builder absentDate(LocalDate absentDate){
            this.absentDate = absentDate;
            return this;
        }

        public Builder reason(String reason){
            this.reason = reason;
            return this;
        }

        public Builder supportingDocumentPath(String supportingDocumentPath){
            this.supportingDocumentPath = supportingDocumentPath;
            return this;
        }

        public Builder status(ExcuseStatus status){
            this.status = status;
            return this;
        }

        public Builder reviewedBy(User reviewedBy){
            this.reviewedBy = reviewedBy;
            return this;
        }

        public Builder submittedDate(LocalDateTime submittedDate){
            this.submittedDate = submittedDate;
            return this;
        }

        public Builder reviewedDate(LocalDateTime reviewedDate){
            this.reviewedDate = reviewedDate;
            return this;
        }

        public ExcuseLetter build(){
            return new ExcuseLetter(excuseId, student, course, absentDate, reason, supportingDocumentPath, status, reviewedBy, submittedDate, reviewedDate);
        }
    }
}
