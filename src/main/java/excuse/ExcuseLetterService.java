package excuse;

import course.Course;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import user.Student;
import user.User;

public class ExcuseLetterService {

    private final ExcuseLetterDAO excuseLetterDAO = new ExcuseLetterDAO();

    public ExcuseLetter submitExcuseLetter(Student student, Course course, LocalDate absentDate, String reason, String supportingDocumentPath, ExcuseStatus status, LocalDateTime submittedDate){
        ExcuseLetter letter = ExcuseLetter.builder()
            .excuseId(0)
            .student(student)
            .course(course)
            .absentDate(absentDate)
            .reason(reason)
            .supportingDocumentPath(supportingDocumentPath)
            .status(status)
            .reviewedBy(null)
            .submittedDate(submittedDate)
            .reviewedDate(null)
            .build();
        return excuseLetterDAO.save(letter);
    }

    public Optional<ExcuseLetter> getExcuseLetterById(int id){
        return excuseLetterDAO.findById(id);
    }

    public List<ExcuseLetter> getAllExcuseLetters(){
        return excuseLetterDAO.findAll();
    }

    public List<ExcuseLetter> getExcuseLettersByStudent(int studentId){
        return excuseLetterDAO.findByStudentId(studentId);
    }

    public List<ExcuseLetter> getExcuseLettersByCourse(int courseId){
        return excuseLetterDAO.findByCourseId(courseId);
    }

    public List<ExcuseLetter> getExcuseLettersByStatus(ExcuseStatus status){
        return excuseLetterDAO.findByStatus(status);
    }

    public List<ExcuseLetter> getExcuseLettersByReviewer(int reviewedByUserId){
        return excuseLetterDAO.findByReviewedBy(reviewedByUserId);
    }

    public boolean reviewExcuseLetter(int excuseId, User reviewedBy, ExcuseStatus newStatus, LocalDateTime reviewedDate){
        Optional <ExcuseLetter> opt = excuseLetterDAO.findById(excuseId);
        if(opt.isPresent()){
            ExcuseLetter old = opt.get();
            ExcuseLetter updated = ExcuseLetter.builder()
                .excuseId(old.excuseId())
                .student(old.student())
                .course(old.course())
                .absentDate(old.absentDate())
                .reason(old.reason())
                .supportingDocumentPath(old.supportingDocumentPath())
                .status(newStatus)
                .reviewedBy(reviewedBy)
                .submittedDate(old.submittedDate())
                .reviewedDate(reviewedDate)
                .build();
            return excuseLetterDAO.update(updated);
        }
        return false;
    }

    public boolean deleteExcuseLetter(int id){
        return excuseLetterDAO.deleteById(id);
    }
}
