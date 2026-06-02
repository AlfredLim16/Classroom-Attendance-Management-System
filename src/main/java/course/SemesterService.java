package course;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class SemesterService {

    private final SemesterDAO semesterDAO = new SemesterDAO();

    public Semester createSemester(String semesterName, String schoolYear, LocalDate startDate, LocalDate endDate){
        Semester semester = new Semester(0, semesterName, schoolYear, startDate, endDate);
        return semesterDAO.save(semester);
    }

    public Optional<Semester> getSemesterById(int id){
        return semesterDAO.findById(id);
    }

    public List<Semester> getAllSemesters(){
        return semesterDAO.findAll();
    }

    public boolean updateSemester(Semester semester){
        return semesterDAO.update(semester);
    }

    public boolean deleteSemester(int id){
        return semesterDAO.deleteById(id);
    }
}
