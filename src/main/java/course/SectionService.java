package course;

import java.util.List;
import java.util.Optional;

public class SectionService {

    private final SectionDAO sectionDAO = new SectionDAO();

    public Section createSection(Program program, YearLevel yearLevel, String sectionCode){
        Section section = new Section(0, program, yearLevel, sectionCode);
        return sectionDAO.save(section);
    }

    public Optional<Section> getSectionById(int id){
        return sectionDAO.findById(id);
    }

    public List<Section> getAllSections(){
        return sectionDAO.findAll();
    }

    public List<Section> getSectionsByProgram(int programId){
        return sectionDAO.findByProgramId(programId);
    }

    public boolean updateSection(Section section){
        return sectionDAO.update(section);
    }

    public boolean deleteSection(int id){
        return sectionDAO.deleteById(id);
    }
}
