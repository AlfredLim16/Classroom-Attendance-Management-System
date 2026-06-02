package user;

import course.Section;
import course.Semester;
import java.util.List;
import java.util.Optional;

public class ProfessorSectionService {

    private final ProfessorSectionDAO professorSectionDAO = new ProfessorSectionDAO();

    public ProfessorSection assignProfessorToSection(Professor professor, Section section, Semester semester, boolean isProfessorRecording){
        ProfessorSection ps = ProfessorSection.builder()
            .professorSectionId(0)
            .professor(professor)
            .section(section)
            .semester(semester)
            .professorRecording(isProfessorRecording)
            .build();
        return professorSectionDAO.save(ps);
    }

    public Optional<ProfessorSection> getProfessorSectionById(int id){
        return professorSectionDAO.findById(id);
    }

    public List<ProfessorSection> getAllProfessorSections(){
        return professorSectionDAO.findAll();
    }

    public List<ProfessorSection> getSectionsByProfessor(int professorId){
        return professorSectionDAO.findByProfessorId(professorId);
    }

    public List<ProfessorSection> getProfessorsBySection(int sectionId){
        return professorSectionDAO.findBySectionId(sectionId);
    }

    public boolean updateProfessorSection(ProfessorSection ps){
        return professorSectionDAO.update(ps);
    }

    public boolean deleteProfessorSection(int id){
        return professorSectionDAO.deleteById(id);
    }
}
