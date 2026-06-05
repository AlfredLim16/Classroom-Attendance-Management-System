package services;

import core.Course;
import core.Student;
import core.User;
import dao.*;
import exceptions.DuplicateEntryException;
import exceptions.NotFoundException;
import junction.ExcuseLetter;
import lookup.ExcuseStatus;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles filing, reviewing, and querying excuse letters.
 */
public class ExcuseLetterService {

    private final ExcuseLetterDAO excuseLetterDAO;

    public ExcuseLetterService() {
        ProgramDAO programDAO         = new ProgramDAOImpl();
        SemesterDAO semesterDAO       = new SemesterDAOImpl();
        SectionDAO sectionDAO         = new SectionDAOImpl(programDAO);
        CourseDAO courseDAO           = new CourseDAOImpl(programDAO, semesterDAO);
        UserDAO userDAO               = new UserDAOImpl();
        StudentDAO studentDAO         = new StudentDAOImpl(userDAO, programDAO, sectionDAO);
        this.excuseLetterDAO          = new ExcuseLetterDAOImpl(studentDAO, courseDAO, userDAO);
    }

    public ExcuseLetterService(ExcuseLetterDAO excuseLetterDAO) {
        this.excuseLetterDAO = excuseLetterDAO;
    }

    /**
     * Student files a new excuse letter. Status starts as PENDING.
     */
    public void fileExcuseLetter(Student student, Course course, LocalDate absentDate,
                                  String reason, String documentPath)
            throws SQLException, DuplicateEntryException {

        ExcuseLetter letter = ExcuseLetter.builder()
                .student(student)
                .course(course)
                .absentDate(absentDate)
                .reason(reason)
                .supportingDocumentPath(documentPath)
                .status(ExcuseStatus.PENDING)
                .reviewedBy(null)
                .submittedDate(LocalDateTime.now())
                .reviewedDate(null)
                .build();

        excuseLetterDAO.insert(letter);
    }

    /**
     * Admin/Professor reviews (approves or rejects) an excuse letter.
     */
    public void reviewExcuseLetter(int excuseId, ExcuseStatus decision, User reviewedBy)
            throws SQLException, NotFoundException {

        ExcuseLetter existing = excuseLetterDAO.findById(excuseId);

        ExcuseLetter updated = ExcuseLetter.builder()
                .excuseId(existing.excuseId())
                .student(existing.student())
                .course(existing.course())
                .absentDate(existing.absentDate())
                .reason(existing.reason())
                .supportingDocumentPath(existing.supportingDocumentPath())
                .status(decision)
                .reviewedBy(reviewedBy)
                .submittedDate(existing.submittedDate())
                .reviewedDate(LocalDateTime.now())
                .build();

        excuseLetterDAO.update(updated);
    }

    public List<ExcuseLetter> getByStudent(int studentId) throws SQLException {
        return excuseLetterDAO.findByStudent(studentId);
    }

    public List<ExcuseLetter> getByStudentAndCourse(int studentId, int courseId) throws SQLException {
        return excuseLetterDAO.findByStudentAndCourse(studentId, courseId);
    }

    public List<ExcuseLetter> getPending() throws SQLException {
        return excuseLetterDAO.findByStatus(ExcuseStatus.PENDING);
    }

    public List<ExcuseLetter> getAll() throws SQLException {
        return excuseLetterDAO.findAll();
    }
}
